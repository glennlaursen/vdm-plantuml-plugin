package vdmj.commands.VDM2UML;

import com.fujitsu.vdmj.tc.definitions.TCClassList;
import com.fujitsu.vdmj.typechecker.PublicClassEnvironment;

public class ClassBuilder 
{
    public StringBuilder types;
    public StringBuilder vars;
    public StringBuilder vals;
    public StringBuilder ops;
    public StringBuilder funcs;

    public static PublicClassEnvironment env;

    public ClassBuilder(TCClassList classList)
    {
        types = new StringBuilder(); 
        vars = new StringBuilder();
        vals = new StringBuilder();
        ops = new StringBuilder();
        funcs = new StringBuilder();
        env = new PublicClassEnvironment(classList);
    }
}