/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SeleniumLogger;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import XMLConfig.XmlConfigurationClass;

public class MessageSettings
{
    public boolean ShowLineNumbers;
    public boolean WatchdogStart;
    public boolean WatchdogEnd;
    public int LineNum;
    public boolean Debug;
    public boolean Pass;
    public boolean Fail;
    public boolean Warning;
    public boolean Error;
	public boolean Fatal;
    public int Indent;
    public int Unindent;
    public boolean Root;
    public Color RGB;
    public Color DefaultRGB;
    public String Image;
    public String File;
    public String Path;
    public String Tab;
    public String TimestampFormat = "HH:mm:ss.fff";
    public String MessageStr;
    public String FormattingStr;
    public IndentModel indentModel = new IndentModel();
    public boolean EnableLogging;
    public String Padding;
    public String pad;
    public String MessageType;
    
    private final int _PrevLineNum;
    private final int _RunningIndentLevel;
    private final Stack _BranchStack = new Stack(); // stack of branches which form a path
    private final Stack _PathsStack = new Stack(); // stack of paths
    

    /// <summary>
    /// Constructor
    /// </summary>
    public MessageSettings()
    {
        ShowLineNumbers = false;
        _PrevLineNum = 0;
        LineNum = 1;
        _RunningIndentLevel = 0;
        //Indent = 0;
        //Unindent = 0;
        Padding = "";
        pad = "   ";
        MessageType = "";
        ResetDefaultValues();
        EnableLogging = true;
    }

    private void ResetDefaultValues()
    {
        WatchdogStart = false;
        WatchdogEnd = false;
        Debug = false;
        Pass = false;
        Fail = false;        
        Warning = false;
        Error = false;
		Fatal = false;
        Indent = 0;
        Unindent = 0;
        Root = false;
        RGB = null;
        DefaultRGB = null;
        Image = null;
        File = null;
        //_TimestampFormat = "HH:mm:ss.fff";
        Path = null;
        Tab = null;
        MessageStr = null;
    }

    public int CurrentIndentLevel()
    {
        return indentModel.getCurrentLevel();
    }
    
    public int GetPendingLevel()
    {

        Stack tmpMessageStack = new Stack();
        String ReturnString = "";
        String PathString = "";
        String FormattingString = "";


        if (Indent > 0)
        {
            for (int i = 0; i < Indent; i++)
            {
                tmpMessageStack.push("INDENT");
            }

            //if (Indent > 1)
            //{
            //    CurrentIndentLevel = CurrentIndentLevel - Indent + 1;  //adjust, because INDENT;INDENT;INDENT really only causes one indent.
            //}
            //Indent = 0;
        }

        if (Unindent > 0)
        {
            for (int i = 0; i < Unindent; i++)
            {
                tmpMessageStack.push("UNINDENT");
            }
            //Unindent = 0;
        }

        if (Root == true)
        {
            tmpMessageStack.push("ROOT");
            //Root = false;
        }



        // Form the FormattingString    
        for (Object StackObj : tmpMessageStack)
        {
            //if (obj.ToString
            FormattingString = (StackObj.toString() + ";" + FormattingString).replaceAll("^;+", "").replaceAll(";+$", "");
        }


        // Form ReturnString
        DateFormat df = new SimpleDateFormat(TimestampFormat);
        if (FormattingString == null || FormattingString.isEmpty())
        {
            ReturnString = "<" + "TIMESTAMP:" + df.format(new Date()) + ">" + MessageStr;
        }
        else
        {
            ReturnString = "<" + FormattingString + ";TIMESTAMP:" + df.format(new Date()) + ">" + MessageStr;
        }

        //ResetDefaultValues();

        indentModel.CalculatePendingLevel(ReturnString);
        //Console.WriteLine("GET_PENDING2: indentModel.CurrentLevel = " + indentModel.CurrentLevel + ", indentModel.PendingLevel = " + indentModel.PendingLevel);
        return indentModel.getCurrentLevel() + indentModel.getPendingDelta(); //NET PENDING LEVEL
        //return indentModel.PendingLevel;
    }
    
    @SuppressWarnings("unchecked")
	public String FormMessageString(boolean RICH_TEXT)
    {
        Stack MessageStack = new Stack();
        String ReturnString = "";
        String ReturnString2 = "";
        String MessageString2 = "";
        String SimpleMessageType = "INFO ";
        String PathString = "";
        String FormattingString = "";
        XmlConfigurationClass config = XmlConfigurationClass.Instance();

        // Step 1: Write flags
        if (WatchdogStart == true)
        {
            MessageStack.push("WATCHDOG_START");
            Pass = false;
        }

        if (WatchdogEnd == true)
        {
            MessageStack.push("WATCHDOG_END");
            Pass = false;
        }

        if (Debug == true)
        {
            //MessageStack.Push("DEBUG");
            Debug = false;
            SimpleMessageType = "DEBUG";
        }
        
        if (Pass == true)
        {
            MessageStack.push("PASS");
            Pass = false;
            SimpleMessageType = "PASS ";
        }

        if (Fail == true)
        {
            MessageStack.push("FAIL");
            Fail = false;
            SimpleMessageType = "FAIL ";
        }

        if (Warning == true)
        {
            MessageStack.push("WARNING");
            Warning = false;
            SimpleMessageType = "WARN ";
        }

        if (Error == true)
        {
            MessageStack.push("ERROR");
            Error = false;
            SimpleMessageType = "ERROR";
        }

		if (Fatal == true)
		{
			MessageStack.push("ERROR");
			Fatal = false;
			SimpleMessageType = "FATAL";
		}
				
        if (Indent > 0)
        {
            for (int i = 0; i < Indent; i++)
            {
                MessageStack.push("INDENT");
            }

            if (Indent > 1)
            {
                indentModel.setCurrentLevel(indentModel.getCurrentLevel() - Indent + 1);
            }
            Indent = 0;
        }

        if (Unindent > 0)
        {
            for (int i = 0; i < Unindent; i++)
            {
                MessageStack.push("UNINDENT");
            }
            Unindent = 0;
        }

        if (Root == true)
        {
            MessageStack.push("ROOT");
            Root = false;
        }

        if (RGB != null)
        {
            MessageStack.push("RGB:" + RGB.getRed() + "," + RGB.getGreen() + "," + RGB.getBlue());
            RGB = null;
        }

        if (DefaultRGB != null)
        {
            MessageStack.push("DEFAULT_RGB:" + DefaultRGB.getRed() + "," + DefaultRGB.getGreen() + "," + DefaultRGB.getBlue());
            DefaultRGB = null;
        }

        if ((Image != null) && (!Image.isEmpty()))
        {
            MessageStack.push("IMAGE:" + Image);
            Image = "";
        }

        if ((File != null) && (!File.isEmpty()))
        {
            MessageStack.push("FILE:" + File);
            File = "";
        }

        if ((Path != null) && (!Path.isEmpty()))
        {
            MessageStack.push("PATH:" + Path);
            Path = "";
        }

        if ((Tab != null) && (!Tab.isEmpty()))
        {
            MessageStack.push("TAB:" + Tab);
            Tab = "";
        }


        // Form the FormattingString    
        for (Object StackObj : MessageStack)
        {
            //if (obj.ToString
            FormattingString = (StackObj.toString() + ";" + FormattingString).replaceAll("^;+", "").replaceAll(";+$", "");
        }

        // Add line numbers
        if (ShowLineNumbers == true)
        {
            MessageStr = "Line " + Integer.toString(LineNum++) + ":   " + MessageStr;
        }
        else
        {
            LineNum++;
        }

        // Form ReturnString
        DateFormat df = new SimpleDateFormat(TimestampFormat);
        if (FormattingString == null || FormattingString.isEmpty())
        {
            ReturnString = "<" + "TIMESTAMP:" + df.format(new Date()) + ">" + MessageStr;
        }
        else
        {
            ReturnString = "<" + FormattingString + ";TIMESTAMP:" + df.format(new Date()) + ">" + MessageStr;
        }
        
        MessageString2 = MessageStr;

        ResetDefaultValues();

        if (EnableLogging) indentModel.SimulateIndentations(ReturnString);

        if (!RICH_TEXT)
        {
            for (int i = 0; i < indentModel.getCurrentLevel(); i++)
            {
                Padding = Padding + pad;
            }
            ReturnString2 = df.format(new Date()) + "	|	" + SimpleMessageType + "	| " + Padding + MessageString2;
            Padding = "";
            return ReturnString2;
        }
        else
        {
            return ReturnString;
        }
    }

}