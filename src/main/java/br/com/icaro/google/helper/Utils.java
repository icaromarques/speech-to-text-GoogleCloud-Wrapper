package br.com.icaro.google.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;

/**
 * @author icaroafonso
 * 
 */
public class Utils {
	
	 
	/**
	 * Transforma um inputStream em um array de bytes
	 * @param is - InputStrem que será transformada em bytes
	 * @return - Um array de bytes com os dados do inputStream
	 * @throws IOException
	 */
	public static byte[] getBytes(InputStream is) throws IOException {

		    int len;
		    int size = 1024;
		    byte[] buf;

		    if (is instanceof ByteArrayInputStream) {
		      size = is.available();
		      buf = new byte[size];
		      len = is.read(buf, 0, size);
		    } else {
		      ByteArrayOutputStream bos = new ByteArrayOutputStream();
		      buf = new byte[size];
		      while ((len = is.read(buf, 0, size)) != -1)
		        bos.write(buf, 0, len);
		      buf = bos.toByteArray();
		    }
		    return buf;
	}
	
	
	/**
	 * Converte um arquivo de audio no formato FLAC - Free Lossless Audio Codec
	 * que é o formato aceito pelo gCloud para a transcrição de audios
	 * 
	 * @param inputFilePath
	 * @return
	 */
	public static File convertAudioToFlac(String inputFilePath) {
		try {                             
			
			 File source = new File(inputFilePath);		                 
			 File target = new File(source.getPath().substring(0, source.getPath().indexOf(source.getName())) +source.getName().substring(0, source.getName().indexOf("."))+".flac");                         
			                                                             
			 target.deleteOnExit();
			 
			     //Audio Attributes                                       
			 AudioAttributes audio = new AudioAttributes();              
			 audio.setCodec("flac");                               
			 audio.setBitRate(16000);                                   
			 audio.setChannels(1);                                       
			 audio.setSamplingRate(44100);                               
			                                                             
			 //Encoding attributes                                       
			 EncodingAttributes attrs = new EncodingAttributes();        
			 attrs.setFormat("flac");                                     
			 attrs.setAudioAttributes(audio);                            
			                                                             
			 //Encode                                                    
			 Encoder encoder = new Encoder();                            
			 encoder.encode(new MultimediaObject(source), target, attrs);
			 return target;
			                                                              
			} catch (Exception ex) {                                      
			 ex.printStackTrace();                   
			 return null;			                                          
			}              
	}
}
