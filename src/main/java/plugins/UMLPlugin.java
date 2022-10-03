package plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCClassList;

import json.JSONArray;
import json.JSONObject;
import lsp.Utils;
import plugins.UML2VDM.Uml2vdmMain;
import plugins.VDM2UML.PlantBuilder;
import plugins.VDM2UML.UMLGenerator;
import rpc.RPCErrors;
import rpc.RPCMessageList;
import rpc.RPCRequest;
import workspace.Diag;
import workspace.EventHub;
import workspace.EventListener;
import workspace.PluginRegistry;
import workspace.events.LSPEvent;
import workspace.events.UnknownTranslationEvent;
import workspace.plugins.AnalysisPlugin;
import workspace.plugins.TCPlugin;

public class UMLPlugin extends AnalysisPlugin implements EventListener {

	public UMLPlugin() {
		super();
	}

	@Override
	public String getName() {
		return "UML";
	}

	@Override
	public void init() {
		EventHub.getInstance().register(UnknownTranslationEvent.class, this);
	}

	@Override
	public RPCMessageList handleEvent(LSPEvent event) throws Exception {

		if (event instanceof UnknownTranslationEvent)
		{
			UnknownTranslationEvent ute = (UnknownTranslationEvent)event;
			
			if (ute.languageId.equals("uml2vdm"))
			{
				return analyseUML2VDM(event.request);
			}
			else if (ute.languageId.equals("vdm2uml"))
			{
				return analyseVDM2UML(event.request);
			}
		}
		
		return null;	// Not handled
	}


	public RPCMessageList analyseUML2VDM(RPCRequest request)
	{
		try
		{
			JSONObject params = request.get("params");

			File uri = Utils.uriToFile(params.get("uri"));
			File saveUri = Utils.uriToFile(params.get("saveUri"));
			Uml2vdmMain puml = new Uml2vdmMain(uri, saveUri);
			puml.run();

			return new RPCMessageList(request, new JSONObject("uri", saveUri.toURI().toString()));
		}
		
		catch (Exception e)
		{
			Diag.error(e);
			return new RPCMessageList(request, RPCErrors.InternalError, e.getMessage());
		}
	}

	public RPCMessageList analyseVDM2UML(RPCRequest request)
	{
		boolean isProject = false;
		try
		{
			JSONObject params = request.get("params");
			File saveUri = Utils.uriToFile(params.get("saveUri"));
			URI uri = params.get("uri");
			String rootUri = uri.toString();

			if(uri != null){
				isProject = Files.isDirectory(Path.of(uri));
			}
				

			
			TCPlugin tcPlugin = PluginRegistry.getInstance().getPlugin("TC");
			TCClassList classes = tcPlugin.getTC();

			if (classes == null || classes.isEmpty())
			{
				return new RPCMessageList(request, RPCErrors.InvalidRequest, "No classes were found");
			}

			PlantBuilder pBuilder = new PlantBuilder(classes);

			if(isProject){
				for (TCClassDefinition cdef: classes)
				{
					cdef.apply(new UMLGenerator(), pBuilder);
				}
			}else{
				String cname = Path.of(uri).getFileName().toString();
				for (TCClassDefinition cdef: classes)
				{
					cdef.apply(new UMLGenerator(), pBuilder);
				}
				
			}

			StringBuilder boiler = UMLGenerator.buildBoiler();

			String projectName = rootUri.substring(rootUri.lastIndexOf('/') + 1);
			
			File outfile = new File(saveUri, projectName + ".puml");
			PrintWriter out = new PrintWriter(outfile);
			try (BufferedWriter writer = new BufferedWriter(out)) 
			{
				writer.append(boiler);
				writer.append(pBuilder.defs);
				writer.append(pBuilder.asocs);
				writer.append("\n");
				writer.append("@enduml");
			}
			out.close();

			return new RPCMessageList(request, new JSONObject("uri", saveUri.toURI().toString()));
		}
		catch (Exception e)
		{
			Diag.error(e);
			return new RPCMessageList(request, RPCErrors.InternalError, e.getMessage());
		}
	}

	@Override
	public void setServerCapabilities(JSONObject capabilities)
	{
		JSONObject experimental = capabilities.get("experimental");
		
		if (experimental != null)
		{
			JSONObject provider = experimental.get("translateProvider");
			
			if (provider != null)
			{
				JSONArray ids = provider.get("languageId");
				
				if (ids != null)
				{
					ids.add("vdm2uml");
					ids.add("uml2vdm");
				}
			}
		}
	}
}
