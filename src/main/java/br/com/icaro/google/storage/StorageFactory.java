package br.com.icaro.google.storage;


import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;

/**
 * @author icaroafonso
 * Esta classe gerencia os detalhes para criar um serviço de armazenamento, incluindo auth.
 */
public class StorageFactory {
  private static Storage instance = null;

  /**
 * @parama ppName - nome do aplictivo a ser criado
 * @param credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
 * @return
 * @throws IOException
 * @throws GeneralSecurityException
 */
public static synchronized Storage getService(String appName,String credentialsFilePath) throws IOException, GeneralSecurityException {
    if (instance == null) {
      instance = buildService(appName,credentialsFilePath);
    }
    return instance;
  }

  /**
 * @param appName - nome do aplictivo a ser criado
 * @param credentialsFilePath - endereço do arquivo JSON com as credenciais do projeto
 * @return
 * @throws IOException
 * @throws GeneralSecurityException
 */
private static Storage buildService(String appName,String credentialsFilePath) throws IOException, GeneralSecurityException {
    HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
    JsonFactory jsonFactory = new JacksonFactory();
    FileInputStream credentialsStream = new FileInputStream(credentialsFilePath);
    
    GoogleCredential credential = GoogleCredential.fromStream(credentialsStream, transport, jsonFactory);// getApplicationDefault(transport, jsonFactory);

      if (credential.createScopedRequired()) {
      Collection<String> scopes = StorageScopes.all();
      credential = credential.createScoped(scopes);
    }

    return new Storage.Builder(transport, jsonFactory, credential)
        .setApplicationName(appName)
        .build();
  }
}
// [END authentication_application_default_credentials]
