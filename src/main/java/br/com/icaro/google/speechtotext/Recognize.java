package br.com.icaro.google.speechtotext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.OperationFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.WordInfo;
import com.google.longrunning.Operation;
import com.google.protobuf.ByteString;

import br.com.icaro.google.helper.Utils;

/**
 * @author icaroafonso
 *
 *  Classe que contém os métodos para fazer o Speech to Text utilizando a API do Google Cloud
 */
public class Recognize {


	/**
	 * Executa o reconhecimento de voz no áudio enviado sincronamente e retorna uma string com o texto lido.
	 * Apenas áudios com menos de um minuto pode ser lidos sincronamente. Para outros áudios ver método assíncrono.
	 *
	 * @param  fileName - o caminho para um arquivo de áudio local para transcrever.
	 * @param  language - A lingua em que está o áudio. Usar no formato "pt-BR" "en-US" etc.
	 * @param  audioEncoding - Codificação de dados de áudio enviados nas mensagens de reconhecimento de áudio.
	 * @param  sampleRateHertz - Taxa de amostragem em Hertz dos dados de áudio enviados. Valores válidos são: 8000-48000. 16000 é ótimo.
	 * @param  profanityFilter - filtro anti-palavrões. verdadeiro - ativa o filtro.
	 * @param  credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
	 * @return String - Texto lido no audio.
	 * @throws Exception
	 * @throws IOException
	 */

	public static String syncRecognizeFile(String fileName, String language, AudioEncoding audioEncoding, int sampleRateHertz, Boolean profanityFilter,String credentialsFilePath) throws Exception, IOException {

		SpeechSettings speechSettings = 
				SpeechSettings.newBuilder()
					.setCredentialsProvider(FixedCredentialsProvider.create(GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath))))
					.build();		

		SpeechClient speech = SpeechClient.create(speechSettings);	
		 
		
		File file = new File(fileName); 
		byte[] sendBuf = Utils.getBytes(new FileInputStream(file));     
		ByteString audioBytes = ByteString.copyFrom(sendBuf);


		RecognitionConfig config = RecognitionConfig.newBuilder()
				.setEncoding(audioEncoding)
				.setLanguageCode(language)
				.setSampleRateHertz(sampleRateHertz)
				.setProfanityFilter(profanityFilter)
				.build();
		RecognitionAudio audio = RecognitionAudio.newBuilder()
				.setContent(audioBytes)
				.build();


		RecognizeResponse response = speech.recognize(config, audio);
		List<SpeechRecognitionResult> results = response.getResultsList();
		String returnString = new String();
		for (SpeechRecognitionResult result: results) {

			SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
			returnString = alternative.getTranscript();
		}
		speech.close();
		return returnString;
	}

	/**
	 * Executa o reconhecimento de voz no áudio enviado sincronamente e retorna uma lista de palavras lidas, com seu início e fim no audio.
	 * Apenas áudios com menos de um minuto pode ser lidos sincronamente. Para outros áudios ver método assíncrono.
	 * 
	 * @param  fileName - o caminho para um arquivo de áudio local para transcrever.
	 * @param  language - A lingua em que está o áudio. Usar no formato "pt-BR" "en-US" etc.
	 * @param  audioEncoding - Codificação de dados de áudio enviados nas mensagens de reconhecimento de áudio.
	 * @param  sampleRateHertz - Taxa de amostragem em Hertz dos dados de áudio enviados. Valores válidos são: 8000-48000. 16000 é ótimo.
	 * @param  profanityFilter - filtro anti-palavrões. verdadeiro - ativa o filtro.
	 * @param  credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
	 * 
	 * @return List {@link ItemAudio} - Lista com as palavras lidas no audio, e quando ela foi dita do mesmo (inicio e fim).
	 * @throws Exception
	 * @throws IOException
	 */
	public static List<ItemAudio> syncRecognizeFileWords(String fileName, String language, AudioEncoding audioEncoding, int sampleRateHertz, Boolean profanityFilter,String credentialsFilePath) throws Exception, IOException {

		SpeechSettings speechSettings = 
				SpeechSettings.newBuilder()
					.setCredentialsProvider(FixedCredentialsProvider.create(GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath))))
					.build();		

		SpeechClient speech = SpeechClient.create(speechSettings);	
		
		File file = new File(fileName);
		byte[] sendBuf = Utils.getBytes(new FileInputStream(file));     
		ByteString audioBytes = ByteString.copyFrom(sendBuf);


		RecognitionConfig config = RecognitionConfig.newBuilder()
				.setEncoding(audioEncoding)
				.setLanguageCode(language)
				.setSampleRateHertz(sampleRateHertz)
				.setEnableWordTimeOffsets(true)
				.setProfanityFilter(profanityFilter)
				.build();
		RecognitionAudio audio = RecognitionAudio.newBuilder()
				.setContent(audioBytes)
				.build();

		// Use blocking call to get audio transcript
		RecognizeResponse response = speech.recognize(config, audio);
		List<SpeechRecognitionResult> results = response.getResultsList();
		List<ItemAudio> returnList = new ArrayList<ItemAudio>();
		for (SpeechRecognitionResult result: results) {
			SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
			for (WordInfo wordInfo: alternative.getWordsList()) {
				ItemAudio item = new ItemAudio();
				item.setPalavra(wordInfo.getWord()); 
				item.setInicio(wordInfo.getStartTime().getSeconds()+"."+ wordInfo.getStartTime().getNanos() / 100000000);
				item.setFim(wordInfo.getEndTime().getSeconds()+"."+ wordInfo.getEndTime().getNanos() / 100000000);
				returnList.add(item);
			}
		}
		speech.close();
		return returnList;
	}


	/**
	 * Realiza reconhecimento de fala no arquivo FLAC (Encoding obrigatório neste caso) remoto (localizado no google storage) e retorna a transcrição.
	 * Apenas áudios com menos de um minuto pode ser lidos sincronamente. Para outros áudios ver método assíncrono.
	 * 
	 * @param gcsUri o caminho para o arquivo de áudio remoto para transcrever. Deve ser sempre contido no google cloud storage, sob o formato 
	 * gs://INTERVALO_CRIADO/nome_do_arquivo_de_audio.
	 * @param  language - A lingua em que está o áudio. Usar no formato "pt-BR" "en-US" etc.
	 * @param  sampleRateHertz - Taxa de amostragem em Hertz dos dados de áudio enviados. Valores válidos são: 8000-48000. 16000 é ótimo. 
	 * @param  audioEncoding - Codificação de dados de áudio enviados nas mensagens de reconhecimento de áudio.
	 * @param  profanityFilter - filtro anti-palavrões. verdadeiro - ativa o filtro.
	 * @param  credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
	 * 
	 * @return String - Texto lido no audio.
	 */
	public static String syncRecognizeGcs(String gcsUri,String language,AudioEncoding audioEncoding, int sampleRateHertz, Boolean profanityFilter,String credentialsFilePath) throws Exception, IOException {
		
		SpeechSettings speechSettings = 
				SpeechSettings.newBuilder()
					.setCredentialsProvider(FixedCredentialsProvider.create(GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath))))
					.build();		

		SpeechClient speech = SpeechClient.create(speechSettings);	

		RecognitionConfig config = RecognitionConfig.newBuilder()
				.setEncoding(audioEncoding)
				.setLanguageCode(language)
				.setSampleRateHertz(sampleRateHertz)
				.setProfanityFilter(profanityFilter)
				.build();
		RecognitionAudio audio = RecognitionAudio.newBuilder()
				.setUri(gcsUri)
				.build();

		RecognizeResponse response = speech.recognize(config, audio);
		List<SpeechRecognitionResult> results = response.getResultsList();
		String returnString = "";
		for (SpeechRecognitionResult result: results) {
			// 
			SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
			returnString = alternative.getTranscript();
		}
		speech.close();
		return returnString;
	}


	/**
	 * Realiza reconhecimento assíncrono no arquivo de audio remoto e retorna uma lista de palavras lidas, com seu início e fim no audio.
	 * 
	 *
	 * @param  gcsUri o caminho para o arquivo de áudio FLAC remoto para transcrever. Deve ser sempre contido no google cloud storage, sob o formato 
	 * gs://INTERVALO_CRIADO/nome_do_arquivo_de_audio.
	 * @param  language - A lingua em que está o áudio. Usar no formato "pt-BR" "en-US" etc.
	 * @param  audioEncoding - Codificação de dados de áudio enviados nas mensagens de reconhecimento de áudio.
	 * @param  sampleRateHertz - Taxa de amostragem em Hertz dos dados de áudio enviados. Valores válidos são: 8000-48000. 16000 é ótimo.
	 * @param  profanityFilter - filtro anti-palavrões. verdadeiro - ativa o filtro.
	 * @param  credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
	 * 
	 * @return List {@link ItemAudio} - Lista com as palavras lidas no audio, e quando ela foi dita do mesmo (inicio e fim).
	 */
	public static List<ItemAudio> asyncRecognizeWords(String gcsUri,String language, AudioEncoding audioEncoding, int sampleRateHertz, Boolean profanityFilter,String credentialsFilePath) throws Exception, IOException {

		SpeechSettings speechSettings = 
				SpeechSettings.newBuilder()
					.setCredentialsProvider(FixedCredentialsProvider.create(GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath))))
					.build();		

		SpeechClient speech = SpeechClient.create(speechSettings);	
		
		RecognitionConfig config = RecognitionConfig.newBuilder()
				.setEncoding(audioEncoding)
				.setLanguageCode(language)
				.setSampleRateHertz(sampleRateHertz)
				.setEnableWordTimeOffsets(true)
				.setProfanityFilter(profanityFilter)
				.build();
		RecognitionAudio audio = RecognitionAudio.newBuilder()
				.setUri(gcsUri)
				.build();

		OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata,
		Operation> response =
		speech.longRunningRecognizeAsync(config, audio);
		while (!response.isDone()) {
			
		}

		List<SpeechRecognitionResult> results = response.get().getResultsList();
		List<ItemAudio> returnList = new ArrayList<ItemAudio>();
		for (SpeechRecognitionResult result: results) {
			// There can be several alternative transcripts for a given chunk of speech. Just use the
			// first (most likely) one here.
			SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);

			for (WordInfo wordInfo: alternative.getWordsList()) {

				ItemAudio item = new ItemAudio();
				item.setPalavra(wordInfo.getWord()); 
				item.setInicio(wordInfo.getStartTime().getSeconds()+"."+ wordInfo.getStartTime().getNanos() / 100000000);
				item.setFim(wordInfo.getEndTime().getSeconds()+"."+ wordInfo.getEndTime().getNanos() / 100000000);
				returnList.add(item);
			}
		}
		speech.close();
		return returnList;
	}

	/**
	 * Realiza reconhecimento assíncrono no arquivo de audio remoto e retorna uma retorna uma string com o texto lido.
	 * 
	 *
	 * @param  gcsUri o caminho para o arquivo de áudio FLAC remoto para transcrever. Deve ser sempre contido no google cloud storage, sob o formato 
	 * gs://INTERVALO_CRIADO/nome_do_arquivo_de_audio.
	 * @param  language - A lingua em que está o áudio. Usar no formato "pt-BR" "en-US" etc.
	 * @param  audioEncoding - Codificação de dados de áudio enviados nas mensagens de reconhecimento de áudio.
	 * @param  sampleRateHertz - Taxa de amostragem em Hertz dos dados de áudio enviados. Valores válidos são: 8000-48000. 16000 é ótimo.
	 * @param  profanityFilter - filtro anti-palavrões. verdadeiro - ativa o filtro.
	 * @param  credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
	 * 
	 * @return String - Texto lido no audio.
	 */
	public static String asyncRecognizeGcs(String gcsUri,String language, AudioEncoding audioEncoding, int sampleRateHertz, Boolean profanityFilter,String credentialsFilePath) throws Exception, IOException {

		SpeechSettings speechSettings = 
				SpeechSettings.newBuilder()
					.setCredentialsProvider(FixedCredentialsProvider.create(GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath))))
					.build();		

		SpeechClient speech = SpeechClient.create(speechSettings);	
		
		RecognitionConfig config = RecognitionConfig.newBuilder()
				.setEncoding(audioEncoding)
				.setLanguageCode(language)
				.setSampleRateHertz(sampleRateHertz)
				.setEnableWordTimeOffsets(true)
				.setProfanityFilter(profanityFilter)
				.build();
		RecognitionAudio audio = RecognitionAudio.newBuilder()
				.setUri(gcsUri)
				.build();
		OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata,
		Operation> response =
		speech.longRunningRecognizeAsync(config, audio);
		while (!response.isDone()) {
			
		}

		List<SpeechRecognitionResult> results = response.get().getResultsList();
		String retorno = new String();
		for (SpeechRecognitionResult result: results) {

			SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
			retorno = alternative.getTranscript();
		}
		speech.close();
		return retorno;
	}


}
