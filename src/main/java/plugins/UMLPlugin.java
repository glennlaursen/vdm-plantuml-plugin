package plugins;

import json.JSONArray;
import json.JSONObject;
import rpc.RPCErrors;
import rpc.RPCMessageList;
import rpc.RPCRequest;
import workspace.Diag;
import workspace.EventHub;
import workspace.EventListener;
import workspace.events.LSPEvent;
import workspace.events.UnknownTranslationEvent;
import workspace.plugins.AnalysisPlugin;

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
		}
		
		return null;	// Not handled
	}


	public RPCMessageList analyseUML2VDM(RPCRequest request)
	{
		try
		{
			JSONObject params = request.get("params");

			return new RPCMessageList(request, new JSONObject("uri", params.toString()));
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
