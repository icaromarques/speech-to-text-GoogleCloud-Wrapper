package br.com.icaro.google.speechtotext;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;

import br.com.icaro.google.speechtotext.ItemAudio;
import br.com.icaro.google.speechtotext.Recognize;

/**
 * Testes para métodos de reconhecimento de texto da Classe Recognize
 */
@RunWith(JUnit4.class)
public class RecognizeTest {
	private static final String PROJECT_ID = "cloud-samples-tests";
	private static final String BUCKET = PROJECT_ID;
	private static final String LANGUAGE = "en-US";
	private static final Integer FREQUENCY = 16000;
	private static final Boolean PROFANITY_FILTER = true;


	// The path to the audio file to transcribe
	private static String fileName = "";
	private static String keysPath = "";

	private String gcsPath = "gs://" + BUCKET + "/speech/brooklyn.flac";




	public RecognizeTest() {
		super();
		fileName = getClass().getClassLoader().getResource("audio.raw").getPath();
		System.out.println(fileName);

		keysPath = getClass().getClassLoader().getResource("keys.json").getPath();
		System.out.println(keysPath);
	}

	@After
	public void tearDown() {
	//	System.setOut(null);
	}

	@Test
	public void testRecognizeFile() throws Exception {
		String got = Recognize.syncRecognizeFile(fileName, LANGUAGE , AudioEncoding.LINEAR16, FREQUENCY,PROFANITY_FILTER,keysPath);
		assertThat(got).contains("how old is the Brooklyn Bridge");
	}

	@Test
	public void testRecognizeWordoffset() throws Exception {
		List<ItemAudio> items = Recognize.syncRecognizeFileWords(fileName, LANGUAGE , AudioEncoding.LINEAR16, FREQUENCY,PROFANITY_FILTER,keysPath); 
		assertThat(items.get(0).getPalavra()).contains("how");
		assertThat(items.get(0).getInicio()).contains("0.0");
	}

	@Test
	public void testRecognizeGcs() throws Exception {
		String got = Recognize.syncRecognizeGcs(gcsPath, LANGUAGE , AudioEncoding.FLAC, FREQUENCY,PROFANITY_FILTER,keysPath);
		assertThat(got).contains("how old is the Brooklyn Bridge");
	}



	@Test
	public void testAsyncRecognizeGcs() throws Exception {
		String got = Recognize.asyncRecognizeGcs(gcsPath, LANGUAGE ,AudioEncoding.FLAC, FREQUENCY,PROFANITY_FILTER,keysPath);
		assertThat(got).contains("how old is the Brooklyn Bridge");
	}

	@Test
	public void testAsyncWordoffset() throws Exception {

		List<ItemAudio> items =  Recognize.asyncRecognizeWords(gcsPath, LANGUAGE ,AudioEncoding.FLAC, FREQUENCY,PROFANITY_FILTER,keysPath);
		assertThat(items.get(0).getPalavra()).contains("how");
		assertThat(items.get(0).getInicio()).contains("0.0");
	}

}
