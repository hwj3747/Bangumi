/*
 * ========================================================
 * ClassName:ObjectAndClass.java
 * Description: 
 * Copyright (C) 2013 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hewj.library.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("rawtypes")
/**
 * 实体类转化成BYTE数据，和将byte数据转化成实体类的方式
  */
public class ObjectAndClass {
	public Object object;
	public Class objectClass;

	public ObjectAndClass(Object object, Class objectClass) {
		this.object = object;
		this.objectClass = objectClass;
	}

	/**对象要继承Serializable接口
	 * @throws Exception */
	public static Serializable ByteToObject(byte[] bytes) throws Exception {
		Object obj = null;
		ByteArrayInputStream bi = null;
		ObjectInputStream oi = null;
		try {
			// bytearray to object
			if(bytes != null){
				bi = new ByteArrayInputStream(bytes);
				oi = new ObjectInputStream(bi);
				obj = oi.readObject();
			}
		} catch (Exception e) {
			System.out.println("translation" + e.getMessage());
			e.printStackTrace();
		}  finally{
			if(bi != null){
				bi.close();
			}
			if(oi != null){
				oi.close();
			}
		}
		return (Serializable) obj;
	}

	/**对象要继承Serializable接口*/
	public static byte[] ObjectToByte(Serializable obj) {
		byte[] bytes = null;  
		try {  
			// object to bytearray  
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);  

			bytes = bo.toByteArray();  

			bo.close();  
			oo.close();  
		} catch (Exception e) {
			System.out.println("translation" + e.getMessage());
			e.printStackTrace();  
		}  
		return bytes;  
	}  
}
