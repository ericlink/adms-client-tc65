// SEND SMS via AT Commands
////////at
////////OK
////////at+cmgf=1
////////OK
////////at+cmgs=2146415465
////////> test message
////////+CMGS: 3
////////
////////OK
//
//        String url = "sms://2146415465"; //full phone number there
//        //Connection conn = Connector.open( url );
//        //Message msg = conn.newMessage( conn.TEXT_MESSAGE );
//        //msg.setPayloadText( "SMS Test" );
//        //conn.send( msg );
//
//        System.out.println("use AT command to send SMS");
//        //text sms format
//        String strRcv = ATCmd.send("\rAT+CMGF=1\r");
//        System.out.println("received: " + strRcv);
//        if (strRcv.indexOf("OK") < 0) throw new RuntimeException("Wrong answer from module");
//        strRcv = ATCmd.send("\rAT+CMGS=2146415465\r");
//        System.out.println("received: " + strRcv);
//        if (strRcv.indexOf("AT+CMGS") < 0) throw new RuntimeException("Wrong answer from module");
//        strRcv = ATCmd.send("Test Message \032\033");
//        System.out.println("received: " + strRcv);
//        if (strRcv.indexOf("ERROR") >= 0) throw new RuntimeException("Wrong answer from module");
//
