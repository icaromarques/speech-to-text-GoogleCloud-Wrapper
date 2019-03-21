package br.com.icaro.google.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.StorageOptions;

/**
 * @author icaroafonso
 *
 */
public class StorageActions {
	
	
	/**
	 * Cria um novo intervalo {@link Bucket} no projeto desejado
	 *
	 * @param projectId - O Id do projeto gCloud onde será criado o bucket
	 * @param bucketName - O nome do intervalo a ser criado
	 * @param credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
	 * @return retorna true se foi criado o intervalo
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public static Boolean createBucket(String projectId,String bucketName, String credentialsFilePath ) throws IOException, GeneralSecurityException {
		 
		Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath));
		 com.google.cloud.storage.Storage storage = StorageOptions
				 .newBuilder()
				 .setCredentials(credentials)
				 .setProjectId(projectId)
				 .build().getService();
		

	    storage.create(BucketInfo.of(bucketName));
	    return true;
	}


	/**
	 * Cria um novo intervalo {@link Bucket} no projeto desejado
	 *
	 * @param projectId - O Id do projeto gCloud onde será deletado o bucket
	 * @param bucketName - O nome do intervalo a ser excluido
	 * @param credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
	 * @return retorna true se foi criado o intervalo
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public static Boolean deleteBucket(String projectId,String bucketName,String credentialsFilePath ) throws FileNotFoundException, IOException {
		
		Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath));
		 com.google.cloud.storage.Storage storage =  StorageOptions
				 .newBuilder()
				 .setCredentials(credentials)
				 .setProjectId(projectId)
				 .build().getService();
		storage.delete(bucketName);
		
		return true;
	}

	/**
	 * Obtém uma lista dos objetos dentro do intervalo (Bucket) fornecido.
	 *
	 * @param applicationName - nome da aplicação/projeto no google cloud.
	 * @param bucketName - o nome do intervalo para listar.
	 * @param credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
	 * @return uma lista dos itens contidos no intervalo {@link Bucket}.
	 */
	public static List<StorageObject> listBucket(String applicationName, String bucketName,String credentialsFilePath ) throws IOException, GeneralSecurityException {
		Storage client = StorageFactory.getService(applicationName,credentialsFilePath);
		Storage.Objects.List listRequest = client.objects().list(bucketName);

		List<StorageObject> results = new ArrayList<StorageObject>();

		Objects objects;
		do {
			objects = listRequest.execute();
			if (objects.getItems() != null)
				results.addAll(objects.getItems());

			listRequest.setPageToken(objects.getNextPageToken());
		} while (null != objects.getNextPageToken());

		return results;
	}


	/**
	 * Obtém metadados do intervalo (Bucket) fornecido.
	 *
	 * @param applicationName - nome da aplicação/projeto no google cloud.
	 * @param bucketName - O nome do intervalo sobre o qual queremos os dados
	 * @param credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
	 * @return um intervalo {@link Bucket} contendo seus metadados.
	 */
	public static Bucket getBucket(String applicationName,String bucketName,String credentialsFilePath ) throws IOException, GeneralSecurityException {
		Storage client = StorageFactory.getService(applicationName,credentialsFilePath);

		Storage.Buckets.Get bucketRequest = client.buckets().get(bucketName);
		// Obtém todas as propriedades do intervalo 
		bucketRequest.setProjection("full");
		return bucketRequest.execute();
	}
	/**
	 * Envia dados para um objeto em um intervalo {@link Bucket}.
	 *
	 * @param applicationName - nome da aplicação/projeto no google cloud.
	 * @param name - Nome do objeto de destino.
	 * @param contentType - extensão do arquivo (MIME Type).
	 * @param file - Arquivo tipo {@link File} para upload.
	 * @param bucketName the name of the bucket to create the object in.
	 * @param credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
	 */
	public static void uploadFile(String applicationName,String name, String contentType, File file, String bucketName, Boolean publicAccess,String credentialsFilePath ) throws IOException, GeneralSecurityException {

		InputStreamContent contentStream = new InputStreamContent(contentType, new FileInputStream(file));
		contentStream.setLength(file.length());

		// insere o objeto já com permissão para todos os usuários. Isto é necessário para que ele seja reconhecido pelo software de leitura de audios
		StorageObject objectMetadata = null;
		if (publicAccess) {
			objectMetadata = new StorageObject()
				.setName(name)
				.setAcl(Arrays.asList(new ObjectAccessControl().setEntity("allUsers").setRole("READER")));
		} else {
			 objectMetadata = new StorageObject().setName(name);
		}

		Storage client = StorageFactory.getService(applicationName,credentialsFilePath );
		Storage.Objects.Insert insertRequest = client.objects().insert(
				bucketName, objectMetadata, contentStream);

		insertRequest.execute();
	}

	/**
	 * Deleta um objeto do intervalo {@link Bucket}.
	 *
	 * @param applicationName - nome da aplicação/projeto no google cloud.
	 * @param path -Caminho para o objeto a ser excluido.
	 * @param bucketName - Intervalo onde o objeto está contido.
	 * @param credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
	 */
	public static void deleteObject(String applicationName,String path, String bucketName,String credentialsFilePath )
			throws IOException, GeneralSecurityException {
		Storage client = StorageFactory.getService(applicationName,credentialsFilePath);
		client.objects().delete(bucketName, path).execute();
	}

}
