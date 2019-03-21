 package br.com.icaro.google.storage;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import com.google.api.services.storage.model.StorageObject;

import br.com.icaro.google.storage.StorageActions;

/**
 * Testes para métodos de reconhecimento de texto da Classe Recognize
 */
@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageActionsTest {

	private static final String APP_NAME = "GCS Samples";
	private static final String PROJECT_ID = "vivid-argon-181713";
	private static final String TEST_OBJECT = "storage-sample-test-upload.txt";
	private static String keysPath;
	private static Path tempPath;
	private static File tempFile;
	private static String bucketName = "my-new-bucket-" + UUID.randomUUID().toString();


	public StorageActionsTest() {
		super();
		keysPath = getClass().getClassLoader().getResource("keys.json").getPath();
		System.out.println(keysPath);
	}

	
	@BeforeClass
	public static void setUp() throws IOException {
		
		
		
	// Create a temp file to upload
			tempPath = Files.createTempFile("StorageSampleTest", "txt");
			Files.write(tempPath, ("This object is uploaded and deleted as part of the "
					+ "StorageSampleTest integration test.").getBytes());
			tempFile = tempPath.toFile();
			tempFile.deleteOnExit();
	}
	
	
	@AfterClass
	public static void tearDown() throws FileNotFoundException, IOException {
		System.setOut(null);
		StorageActions.deleteBucket(PROJECT_ID,bucketName,keysPath);
	}


	@Test
	public void test1CreateBucket()  throws Exception {
		Boolean created = StorageActions.createBucket(PROJECT_ID,bucketName,keysPath);
		assertThat(created).isTrue();
	}
	

	@Test
	public void test2Upload() throws Exception {
		StorageActions.uploadFile(APP_NAME,TEST_OBJECT, "text/plain", tempFile, bucketName,false,keysPath);
		List<StorageObject> listing = StorageActions.listBucket(APP_NAME,bucketName,keysPath);
		List<String> names = listing.stream().map(so -> so.getName()).collect(Collectors.toList());
		assertThat(names).named("objects found after upload").contains(TEST_OBJECT);
	}
	
	
	
	@Test
	public void test3ListBucket() throws Exception {
		List<StorageObject> listing = StorageActions.listBucket(APP_NAME,bucketName,keysPath);
		assertThat(listing).isNotEmpty();
	}
	
	@Test
	public void test4Delete() throws Exception {
		StorageActions.deleteObject(APP_NAME,TEST_OBJECT, bucketName,keysPath);
		List<StorageObject> listing = StorageActions.listBucket(APP_NAME,bucketName,keysPath);
		List<String> names = listing.stream().map(so -> so.getName()).collect(Collectors.toList());
		assertThat(names).named("objects found after delete").doesNotContain(TEST_OBJECT);
	}
}
