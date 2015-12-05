package com.gargon.smarthome.clunet.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author gargon
 */
public class IntelHexReader {

    //See about format: http://microsin.net/programming/PC/intel-hex-file-format.html
    //See about format: http://comments.gmane.org/gmane.comp.hardware.avr.gcc/6938
    private boolean endOfFile = false;

    private int length = 0;
    private byte[] data = new byte[8192];

    public boolean read(String fileName) {

        endOfFile = false;
        length = 0;
        Arrays.fill(data, (byte) 0xFF);

        if (fileName != null) {
            File f = new File(fileName);
            if (f.exists()) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(f));
                    String line;

                    int sbaOffset = 0;
                    length = 0;

                    while ((line = reader.readLine()) != null) {
                        //RECORD MARK (1 byte) + (RECLEN (1 byte) + LOAD OFFSET (2 bytes) + RECTYP (1 byte) + CHKSUM (1 byte)) * 2 = 11
                        if (line.length() >= 11 && line.charAt(0) == ':') {
                            int reclen = Integer.parseInt(line.substring(1, 3), 16);
                            if (line.length() == 11 + reclen * 2) {
                                int offset = Integer.parseInt(line.substring(3, 7), 16);
                                int rectyp = Integer.parseInt(line.substring(7, 9), 16);

                                byte[] tdata = new byte[reclen];
                                for (int i = 0, j = 0; i < reclen; i++, j += 2) {
                                    tdata[i] = (byte) Integer.parseInt(line.substring(9 + j, 11 + j), 16);
                                }
                                int chksum = Integer.parseInt(line.substring(line.length() - 2), 16);

                                //check crc
                                int crcacc = reclen + ((offset >> 8) & 0xFF) + (offset & 0xFF) + rectyp;
                                for (int i = 0; i < reclen; i++) {
                                    crcacc += tdata[i] & 0xFF;
                                }
                                if (chksum == ((0x100 - crcacc) & 0xFF)) {

                                    switch (rectyp) {
                                        case 0: // Data Record
                                            offset += sbaOffset;

                                            //out of buffer -> increase by 2 times
                                            if (offset + reclen > data.length) {
                                                int len = data.length;
                                                data = Arrays.copyOf(data, len + len);
                                                Arrays.fill(data, len, len + len, (byte) 0xFF);
                                            }

                                            System.arraycopy(tdata, 0, data, offset, reclen);
                                            length = Math.max(length, offset + reclen);

                                            break;
                                        case 1: //End of File Record
                                            endOfFile = true;
                                            break;
                                        case 2: //Extended Segment Address Record
                                            if (reclen == 2) {
                                                sbaOffset = ((tdata[0] & 0xFF) << 12) | ((tdata[1] & 0xFF) << 4);
                                            }
                                            break;
                                    }

                                } else {
                                    break;
                                }
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    return endOfFile;
                } catch (FileNotFoundException ex) {
                    //Logger.getLogger(IntelHexReader.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    //Logger.getLogger(IntelHexReader.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException ex) {
                    }
                }
            }

        }
        return false;
    }

    public int getLength() {
        return length;
    }

    public byte[] getData() {
        if (endOfFile) {
            return data;
        }
        return null;
    }

}
