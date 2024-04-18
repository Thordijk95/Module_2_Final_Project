package com.nedap.university.util.Packets;

import static com.nedap.university.util.DatagramProperties.HEADER_SIZE;

import com.nedap.university.Requests;
import com.nedap.university.util.Util;
import java.util.regex.PatternSyntaxException;

public class OutboundPacket extends AbstractPacket{

  // Create an outbound packet with header and data to send
  public OutboundPacket(Requests requestType, boolean firstPacket, boolean acknowledgement, int sequenceNumber, String fileName, byte[] data) {
    setRequestType(requestType);
    setFirstPacket(firstPacket);
    setAcknowledgement(acknowledgement);
    setSequenceNumber(sequenceNumber);
    try {
      if (Util.fileNameRequired(requestType)) {
        String[] file = fileName.split("\\.");
        setFileName(file[0]);
        setFileType(file[1]);
      } else {
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
