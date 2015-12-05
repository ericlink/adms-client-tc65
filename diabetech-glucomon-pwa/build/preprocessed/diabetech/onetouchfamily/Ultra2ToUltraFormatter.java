package net.diabetech.onetouchfamily;

import java.io.ByteArrayOutputStream;
import net.diabetech.util.Logger;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
class Ultra2ToUltraFormatter {

    byte[] oneTouchFormatPayload;
    byte[] ultra2payload;
    static final int HEADER_LENGTH = 33;
    static final int DATA_MARK_START = 42;
    static final int DATA_MARK_END = 51;
    static final int CHECKSUM_START = 54;
    static final int LINE_LENGTH = 62;
    static final char END_OF_LINE_TOKEN = '\n';
    static final char END_OF_LINE_RETURN = '\r';

    byte[] convert(byte[] ultra2payload) {
        this.ultra2payload = ultra2payload;
        processBytes();
        return oneTouchFormatPayload;
    }

    /**
     * Go through byte by byte, 
     * keeping track of current checksum,
     * discard old checksum and bytes that are skipped,
     * only writing the new information with the new checksum
     */
    private void processBytes() {
        Logger.log("Ultra2ToUltraFormatter.processBytes()");

        //indexof to find serial number in first headerX2 bytes
        // if is ultra, return w/ orig payload
        // else process ultra bytes
        //"P 125,"VJN486FAY","MG/DL " 05CB" +
        //"P xxx,"xxxxxxxxY","xxxxxxxxxxxxxxx

// pass all through to server for visibility
//        if (ultra2payload[0] != 'P') {
//            Logger.log( "1st byte of payload must be 'P'; null return value");
//            return;
//        }
//        if (ultra2payload.length < HEADER_LENGTH) {
//            Logger.log( "minimum length not met");
//            return;
//        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int currentLine = 0;
        int checksum = 0;
        int currentLineIdx = 0;
        for (int i = 0; i < ultra2payload.length; i++) {
//            Logger.log("byte", (char) ultra2payload[i]);
//            Logger.log("i", i);
//            Logger.log("currentLine", currentLine);
//            Logger.log("currentLineIdx", currentLineIdx);
//            Logger.log("checksum", checksum);
//            //Logger.logByteArray("baos\n", baos.toByteArray(), true);
//            Logger.log("baos.tostring", new String(baos.toByteArray()));
            if (i < HEADER_LENGTH) {
                os.write(ultra2payload[i]);
                continue;
            }
//            if ((i - HEADER_LENGTH + 1) % LINE_LENGTH + 1 == 0) {
//                currentLine++;
//                currentLineIdx = 0;
//                checksum = 0;
//                if (ultra2payload[i] != 'P') {
//                    throw new RuntimeException("Bad Data");
//                }
//            }
            if (currentLineIdx < DATA_MARK_START) {
                currentLineIdx++;
                os.write(ultra2payload[i]);
                checksum += (int) ultra2payload[i];
                continue;
            }
            if (currentLineIdx >= DATA_MARK_START && currentLineIdx < DATA_MARK_END) {
                currentLineIdx++;
                // skip over the u2 data mark bytes baos.write(ultra2payload[i]);
                continue;
            }
            if (currentLineIdx >= DATA_MARK_END && currentLineIdx < CHECKSUM_START) {
                currentLineIdx++;
                os.write(ultra2payload[i]);
                checksum += (int) ultra2payload[i];
                continue;
            }
            if (currentLineIdx >= CHECKSUM_START) {
                currentLineIdx++;
                if (ultra2payload[i] == END_OF_LINE_TOKEN) {
                    // write new checksum
                    os.write(' ');
                    char[] checksumChars = Integer.toHexString(checksum).toUpperCase().toCharArray();
                    for (int x = 0; x < 4 - checksumChars.length; x++) {
                        os.write('0');
                    }
                    for (int c = 0; c < checksumChars.length; c++) {
                        os.write(checksumChars[c]);
                    }
                    // write end of line
                    os.write(END_OF_LINE_TOKEN);
                    //baos.write(END_OF_LINE_RETURN);
                    currentLine++;
                    currentLineIdx = 0;
                    checksum = 0;
                }
                continue;
            }
        }
        oneTouchFormatPayload = os.toByteArray();
    }
}
//2009-10-14 02:53:08,209 560216952 (JMS SessionPool Worker-36:) Oct 14, 2009 2:53:08 AM net.diabetech.medicaldevice.lifescan.OneTouchUltraParser parse
//SEVERE: Line [P "SAT","08/02/08","04:06:02   ","  097 ", 00 0848] incorrect checksum (0828) expected 0848
//net.diabetech.medicaldevice.lifescan.ParseException: Line [P "SAT","08/02/08","04:06:02   ","  097 ", 00 0848] incorrect checksum (0828) expected 0848
//[s:160:"P 125,"VJN486FAY","MG/DL " 05CB
//P "SAT","08/02/08","04:06:02   ","  097 ", 00 0848
//P "SAT","08/02/08","04:04:32   ","  099 ", 00 084B
//
//P 008,"THT8388GT","MG/DL " 05C1
//P "MON","09/14/09","13:46:08   ","  170 ", 00 0831
//P "SAT","09/12/09","20:02:40   ","  151 ", 00 081E
//P "SAT","09/12/09","20:01:00   ","  121 ", 00 0816
//P "SAT","09/12/09","19:58:08   ","  138 ", 00 083A
//P "SAT","09/12/09","19:55:24   ","  131 ", 00 082E
//P "SAT","06/10/06","10:20:24   ","  154 ", 00 081A
//P "SAT","06/10/06","10:18:08   ","  157 ", 00 0826
//P "WED","12/21/05","12:35:08   ","  124 ", 00 0817
//
//P 125,"VJN486FAY","MG/DL " 05CB
//P "SAT","08/02/08","04:06:02   ","  097 ","N","00", 00 09B6
//P "SAT","08/02/08","04:04:32   ","  099 ","N","00", 00 09B9
//P "SAT","08/02/08","04:03:48   ","  209 ","N","00", 00 09B8
//P "SAT","08/02/08","04:01:24   ","  153 ","N","00", 00 09AE
//P "SAT","08/02/08","03:59:20   ","  077 ","N","00", 00 09BB
//P "FRI","08/01/08","18:47:49   ","  111 ","N","00", 00 09B6
//P "FRI","08/01/08","18:45:52   ","  089 ","N","00", 00 09BC
//P "FRI","08/01/08","18:42:35   ","  069 ","N","00", 00 09B8
//P "FRI","08/01/08","18:39:23   ","  065 ","N","00", 00 09B7
//P "FRI","08/01/08","18:32:23   ","  154 ","N","00", 00 09AF
//P "FRI","08/01/08","18:31:25   ","  155 ","N","00", 00 09B1
//P "FRI","08/01/08","17:37:17   ","  123 ","N","00", 00 09B2
//P "FRI","08/01/08","15:05:35   ","  259 ","N","00", 00 09B5
//P "FRI","08/01/08","15:04:27   ","  227 ","N","00", 00 09B0
//P "THU","07/31/08","08:27:22   ","  081 ","N","00", 00 09C2
////// Server parser below //////
////// Server parser below //////
////// Server parser below //////
////// Server parser below //////
////// Server parser below //////
////// Server parser below //////
////// Server parser below //////
////// Server parser below //////
//package net.diabetech.medicaldevice.lifescan;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.TimeZone;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import net.diabetech.entity.DataPoint;
//import net.diabetech.entity.MedicalDevice;
//import net.diabetech.entity.UnitOfMeasureType;
///**
// * From One Touch Ultra comm spec:
// * DMP
// * HEADER
// * P nnn,“ MeterSN ”,“MG/DL ”
// * (1)     (2)           (3)
// * (1) Number of datalog records to follow (0 – 150)
// * (2) Meter serial number (9 characters)
// * (3) Unit of measure for glucose values
// *
// * Day of week (SUN, MON, TUE, WED, THU, FRI, SAT)
// * Date of reading
// * Time of reading (If two or more readings were taken within the same minute,
// * they will be
// * separated by 8 second intervals)
// *
// * (7) Result format:
// *      “ nnn ” - blood test result (mg/dL)
// *      “ HIGH ” - blood test result >600 mg/dL
// *      “C nnn ” - control solution test result (mg/dL)
// *      “CHIGH ” - control solution test result >600 mg/dL
// *
// **/
//public class OneTouchUltraParser {
//    private static Logger logger = Logger.getLogger(OneTouchUltraParser.class.getName());
//    private String rawData;
//    private int headerRecordCount;
//    private UnitOfMeasureType unitOfMeasure;
//    private String[] lines;
//    private MedicalDevice medicalDevice = new MedicalDevice();
//
//    static final private int HEADER_LENGTH = 31;
//    static final private int LINE_LENGTH = 50;
//    static final private int MIN_RAW_DATA_LENGTH = HEADER_LENGTH;
//
//    public OneTouchUltraParser(String rawData) throws ParseException {
//        if ( rawData == null || rawData.length() < MIN_RAW_DATA_LENGTH ) {
//            throw new ParseException( "Data null or too short [" + rawData + "]" );
//        }
//        this.rawData = rawData;
//        // only create a valid OneTouchUltraParser
//        // object if parse is successful
//        // and an exception hasn't been thrown'
//        parse();
//    }
//
//    /**
//     * Parse and validate the data
//     **/
//    private void parse() throws ParseException {
//        try {
//            parseLines();
//            preParseValidation();
//            parseHeader();
//            parseDataRecords();
//            postParseValidation();
//        } catch ( Throwable t ) {
//            logger.log( Level.SEVERE, t.getMessage(), t );
//            throw new ParseException( t );
//        }
//    }
//
//    private void preParseValidation() throws ParseException {
//        if ( !(lines.length >= 1) ) {
//            throw new ParseException( "Must have at least one line (header is always present)" );
//        }
//        checkLineLength();
//        validateChecksums();
//    }
//
//    private void postParseValidation() throws ParseException {
//        if ( headerRecordCount != medicalDevice.getDataPoints().size() ) {
//            throw new ParseException( "Datarecords parsed (" + medicalDevice.getDataPoints().size() + ") does not match header record count (" + headerRecordCount + ")" );
//        }
//    }
//
//    private void checkLineLength() throws ParseException {
//        // header is required
//        if ( lines[0].length() != HEADER_LENGTH ) {
//            throw new ParseException( "Header incorrect length (" + lines[0].length() + ") expected " + HEADER_LENGTH );
//        }
//
//        // check any lines after header (optional)
//        for (int i = 1; i < lines.length; i++) {
//            if ( lines[i].length() != LINE_LENGTH ) {
//                throw new ParseException( "Line [" + i + "] incorrect length (" + lines[i].length() + ") expected " + LINE_LENGTH );
//            }
//        }
//    }
//
//    private void validateChecksums() throws ParseException {
//        for (String line : lines ) {
//            validateChecksumForLine( line );
//        }
//    }
//
//    private void validateChecksumForLine(String line) throws ParseException {
//        int lastDataCharacter = line.lastIndexOf(" ");
//        String payloadChecksum = line.substring( lastDataCharacter + 1, line.length() );
//        int calculatedChecksum = 0;
//        String dataWithoutChecksum = line.substring(0,lastDataCharacter);
//        for (Character c : dataWithoutChecksum.toCharArray() ) {
//            calculatedChecksum += (int)c;
//        }
//        String calculatedChecksumString = String.format( "%4s", Integer.toHexString(calculatedChecksum).toUpperCase() );
//        calculatedChecksumString = calculatedChecksumString.replace( " ", "0" );
//        if ( !calculatedChecksumString.equals( payloadChecksum ) ) {
//            throw new ParseException( "Line [" + line + "] incorrect checksum (" + calculatedChecksumString + ") expected " + payloadChecksum );
//        }
//    }
//
//    /**
//     * P 150,"QSW421ECT","MG/DL " 05C7
//     **/
//    private void parseHeader() throws ParseException {
//        headerRecordCount = Integer.parseInt( lines[0].substring( 2, 5 ) );
//        medicalDevice.setSerialNumber( lines[0].substring( 7, 16 ) );
//        String unitOfMeasureString = lines[0].substring( 19, 25 ).trim();
//        if ( unitOfMeasureString.equals( "MG/DL" ) ) {
//            this.unitOfMeasure = UnitOfMeasureType.MM_DL;
//        } else {
//            throw new ParseException ( "Unknown Unit of Measure (" + unitOfMeasureString + ") found." );
//        }
//    }
//
//    /**
//     * P "THU","03/03/05","02:00:40   ","  209 ", 00 081F
//     * @see net.diabetech.entity.DataPoint.LifeScanOneTouchUltraFormatter
//     **/
//    private void parseDataRecords() {
//        for (int i = 1; i < lines.length; i++) {
//            boolean isControl = "C".equals( lines[i].substring( 34, 35 ) );
//            String valueString = lines[i].substring( 35, 40 ).trim();
//            int value = 0;
//            boolean isHigh = false;
//            if ( valueString.equals( "HIGH" ) ) {
//                value = 601;
//                isHigh = true;
//            } else if ( valueString.equals( "REXC" ) ) {
//                value = 602;
//                isHigh = true;
//            } else {
//                value = Integer.parseInt( valueString );
//                isHigh = false;
//            }
//
//            // timestamp
//            int month = Integer.parseInt( lines[i].substring(  9, 11 ) );
//            int day   = Integer.parseInt( lines[i].substring( 12, 14 ) );
//            int year  = Integer.parseInt( lines[i].substring( 15, 17 ) );
//            int hour  = Integer.parseInt( lines[i].substring( 20, 22 ) );
//            int min   = Integer.parseInt( lines[i].substring( 23, 25 ) );
//            int sec   = Integer.parseInt( lines[i].substring( 26, 28 ) );
//            Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
//            cal.set(year + 2000, month - 1, day, hour, min, sec);
//            cal.set( Calendar.MILLISECOND,0 );
//
//            DataPoint dp = new DataPoint();
//            dp.setIsControl( isControl );
//            dp.setValue( value );
//            dp.setUnitOfMeasureCode( unitOfMeasure );
//            dp.setTimestamp( cal.getTime() );
//            dp.setOriginated( new Date() );
//
//            medicalDevice.addDataPoint( dp );
//            logger.log( Level.FINE, "Parsed datapoint timestamp={0},value={1},isControl={2},unitOfMeaure={3}", new Object[] { cal.getTime(), value, isControl, unitOfMeasure } );
//        }
//    }
//
//    /**
//     * Remove serial noise from the lines, put them in an array that will be
//     * checked using checksum and line length tests
//     **/
//    private void parseLines() {
//        List<String> cleanLines = new ArrayList<String>();
//        String[] rawLines = rawData.split(  "\n" );
//        for (int i = 0; i < rawLines.length; i++ ) {
//            String cleaned = rawLines[i].trim();
//            if ( cleaned.length() > 0 ) {
//                cleanLines.add( cleaned );
//            }
//        }
//        lines = new String[cleanLines.size()];
//        lines = cleanLines.toArray(lines);
//    }
//
//    public MedicalDevice getMedicalDevice() {
//        return medicalDevice;
//    }
//
//}
//
//

