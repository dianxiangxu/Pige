package pige.dataLayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;


/**
 * @author Alex Charalambous (June 2010): It is common
 * for complex objects 
 * to be cloned. As the default clone procedure only does
 * a shadow copy this class creates a deep copy of any
 * object passed in (given that the object is serializable)
 * Especially useful for undo functions.
 */

public class ObjectDeepCopier {
	public static Object deepCopy(Object obj) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream;
		Object deepCopy = null;
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

			objectOutputStream.writeObject(obj);
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					byteArrayOutputStream.toByteArray());
			ObjectInputStream objectInputStream = new ObjectInputStream(
					byteArrayInputStream);
			deepCopy = objectInputStream.readObject();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return deepCopy;

	}

}
