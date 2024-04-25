package com.nedap.university.Packets;

import static com.nedap.university.util.DatagramProperties.HEADER_SIZE;

import com.nedap.university.Communication.Requests;
import com.nedap.university.util.Util;
import java.net.InetAddress;
import java.util.regex.PatternSyntaxException;
import java.util.zip.CRC32;

public class OutboundPacket extends AbstractPacket{

  // Create an outbound packet with header and data to send
  public OutboundPacket(InetAddress address, int port, Requests requestType, boolean firstPacket, boolean last, boolean acknowledgement, int sequenceNumber, String fileName, byte[] data) {
    setCRC32(calculateCRC(data));
    setRequestType(requestType);
    setFirstPacket(firstPacket);
    setLastPacket(last);
    setAcknowledgement(acknowledgement);
    setSequenceNumber(sequenceNumber);
    setAddress(address);
    setPort(port);
    try {
      if (Util.fileNameRequired(requestType) && Util.validFileName(fileName)) {
        String[] file = fileName.split("\\.");
        setFileName(file[0]);
        setFileType(file[1]);
      } else {
        System.out.println("Filename empty! " + fileName);
        setFileName("");
        setFileType("");
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Splitting filename on . has failed, array index out of bounds!");
    } catch (PatternSyntaxException e) {
      System.out.println("Provided syntax" + fileName + " is incorrect!");
      System.out.println("Filename syntax should be <filename>.<filetype>");
    }
    constructHeader();
    setData(getHeader(), 0, 0, HEADER_SIZE);
    setData(data, 0, HEADER_SIZE, data.length);
  }

}
