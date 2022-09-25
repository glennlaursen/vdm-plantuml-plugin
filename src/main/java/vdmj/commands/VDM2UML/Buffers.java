package vdmj.commands.VDM2UML;

import com.fujitsu.vdmj.tc.definitions.TCClassList;
import com.fujitsu.vdmj.typechecker.PublicClassEnvironment;

public class Buffers 
{
    public StringBuilder defs;
    public StringBuilder vars;
    public StringBuilder vals;
    public StringBuilder ops;
    public StringBuilder funcs;
    public StringBuilder asocs;

    public static PublicClassEnvironment env;

    public Buffers(TCClassList classList)
    {
        defs = new StringBuilder();
        asocs = new StringBuilder();
        env = new PublicClassEnvironment(classList);
    }
}