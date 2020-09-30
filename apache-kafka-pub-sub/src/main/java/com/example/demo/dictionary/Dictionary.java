package com.example.demo.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dictionary {

	private static List<String> words = new ArrayList<>();
	private static Random random = new Random();

	public static void loadData() {

		InputStream inputStream = Dictionary.class.getResourceAsStream("/dictionary.txt");

		InputStreamReader reader = new InputStreamReader(inputStream);
		BufferedReader in = new BufferedReader(reader);
		String strCurrentLine;

		try {
			while ((strCurrentLine = in.readLine()) != null)
					words.add(strCurrentLine);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public static String getRandomWord() {
		int size = words.size();
		int randomLine = random.nextInt(size);
		String randomWord = words.get(randomLine);
		return randomWord;
	}

}
