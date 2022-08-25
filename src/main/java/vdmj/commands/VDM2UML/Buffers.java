package vdmj.commands.VDM2UML;

import com.fujitsu.vdmj.tc.definitions.TCClassList;
import com.fujitsu.vdmj.typechecker.PublicClassEnvironment;

public class Buffers 
{
    public StringBuilder defs;
    public StringBuilder asocs;
    public static PublicClassEnvironment env;

    public Buffers(TCClassList classList)
    {
        defs = new StringBuilder();
        asocs = new StringBuilder();
        env = new PublicClassEnvironment(classList);
    }
}