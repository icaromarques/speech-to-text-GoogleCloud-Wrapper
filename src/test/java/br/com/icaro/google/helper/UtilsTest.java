 package br.com.icaro.google.helper;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import br.com.icaro.google.helper.Utils;

/**
 * Testes para métodos da classe Utils
 */
@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UtilsTest {
 
	@BeforeClass
	public static void setUp()  {
	}
	
	
	@AfterClass
	public static void tearDown() {	
	}


	@Test
	public void test1ConvertFile()  throws Exception {
		File original = new File(getClass().getClassLoader().getResource("itatiaia-teste.mp3").getPath());
		File converted =  Utils.convertAudioToFlac(getClass().getClassLoader().getResource("itatiaia-teste.mp3").getPath());
			
		assertTrue(converted != null);
		assertTrue( original.getPath().substring(0, original.getPath().indexOf(original.getName())).equalsIgnoreCase(
					converted.getPath().substring(0, converted.getPath().indexOf(converted.getName()))));
		assertTrue(converted.getName().contains(".flac"));
		
	}
	

}
