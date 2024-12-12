package com.kt.ui;

import com.kt.channel.ChannelSimulator;
import com.kt.decoding.Decoder;
import com.kt.encoding.Encoder;
import com.kt.helpers.HadamardMatrixGenerator;
import com.kt.helpers.ImageProcessingHelper;
import com.kt.helpers.TextTransformHelper;
import com.kt.helpers.ValidationHelper;
import com.kt.helpers.ChartHelper;
import com.kt.helpers.TransmissionResults;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kt.helpers.ChartHelper.drawGraph;

public class MainUI {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(MainUI::createAndShowGUI);
	}

	// Sukuriamas ir rodomas pagrindinis vartotojo sąsajos langas
	private static void createAndShowGUI() {
		JFrame frame = new JFrame("A5 - Reed-Muller");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);

		// Pagrindinio meniu sudarymas
		JPanel mainMenu = new JPanel();
		mainMenu.setLayout(new GridLayout(4, 1, 10, 10));
		JLabel title = new JLabel("Select a Scenario", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 20));
		mainMenu.add(title);

		// Mygtukai scenarijams pasirinkti
		JButton scenario1Button = new JButton("Scenario 1: Vector Transmission");
		JButton scenario2Button = new JButton("Scenario 2: Text Transmission");
		JButton scenario3Button = new JButton("Scenario 3: Image Transmission");

		// Mygtukų pridėjimas prie pagrindinio meniu
		mainMenu.add(scenario1Button);
		mainMenu.add(scenario2Button);
		mainMenu.add(scenario3Button);

		// Kortelių išdėstymas
		JPanel cards = new JPanel(new CardLayout());
		cards.add(mainMenu, "MainMenu");
		cards.add(createScenario1Panel(cards), "Scenario1");
		cards.add(createScenario2Panel(cards), "Scenario2");
		cards.add(createScenario3Panel(cards), "Scenario3");

		// Mygtukų veiksmai
		scenario1Button.addActionListener(e -> showCard(cards, "Scenario1"));
		scenario2Button.addActionListener(e -> showCard(cards, "Scenario2"));
		scenario3Button.addActionListener(e -> showCard(cards, "Scenario3"));

		frame.add(cards);
		frame.setVisible(true);
	}


	// Pirmasis scenarijus: Vektoriaus perdavimas
	private static JPanel createScenario1Panel(JPanel cards) {
		JPanel panel = new JPanel(new BorderLayout());
		JTextArea output = new JTextArea(20, 50);
		output.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(output);

		// Grįžimo į pagrindinį meniu mygtukas
		JButton backButton = new JButton("Back to Main Menu");
		backButton.addActionListener(e -> showCard(cards, "MainMenu"));

		// Įvesties laukai
		JPanel inputPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel mLabel = new JLabel("Enter m (positive integer >= 1):");
		JTextField mField = new JTextField();
		mField.setPreferredSize(new Dimension(200, 25));
		JLabel mError = new JLabel("");
		mError.setForeground(Color.RED);

		JLabel vectorLabel = new JLabel("Enter binary vector:");
		JTextField vectorField = new JTextField();
		vectorField.setPreferredSize(new Dimension(200, 25));
		JLabel vectorError = new JLabel("");
		vectorError.setForeground(Color.RED);

		JLabel probabilityLabel = new JLabel("Enter error probability (0 <= p <= 1) x.x format:");
		JTextField probabilityField = new JTextField();
		probabilityField.setPreferredSize(new Dimension(200, 25));
		JLabel probabilityError = new JLabel("");
		probabilityError.setForeground(Color.RED);

		JTextArea transmittedDataArea = new JTextArea(3, 50);
		transmittedDataArea.setEditable(true);
		transmittedDataArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JLabel transmittedDataError = new JLabel("");
		transmittedDataError.setForeground(Color.RED);

		// Pridedami mygtukai
		JButton encodeTransmitButton = new JButton("Encode and Transmit");
		JButton decodeButton = new JButton("Decode");

		// Pirmojo mygtuko - užkodavimo ir perdavimo veiksmai
		encodeTransmitButton.addActionListener(e -> {
			boolean valid = true;

			// Atliekamos įvestų laukų validacijos, kurios kreipiasi į ValidationHelper klasę (ten aprašyti validacijos metodai)
			String mText = mField.getText();
			if (!ValidationHelper.isPositiveInteger(mText)) {
				mError.setText("Invalid: Must be a positive integer >= 1");
				valid = false;
			} else {
				mError.setText("");
			}

			String vectorText = vectorField.getText();
			if (!mText.isEmpty() && !ValidationHelper.isValidBinaryVector(vectorText, Integer.parseInt(mText) + 1)) {
				vectorError.setText("Invalid: Must be a binary vector of length " + (Integer.parseInt(mText) + 1));
				valid = false;
			} else {
				vectorError.setText("");
			}

			String probabilityText = probabilityField.getText();
			if (!ValidationHelper.isProbability(probabilityText)) {
				probabilityError.setText("Invalid: Must be a real number between 0 and 1");
				valid = false;
			} else {
				probabilityError.setText("");
			}

			//Jeigu validacija pavyko, vykdomas scenarijus
			if (valid) {
				try {
					// Įvestų duomenų konvertavimas į reikiamus tipus
					int m = Integer.parseInt(mText);
					double probability = Double.parseDouble(probabilityText);
					// Sukuriamas atsitiktinių skaičių generatorius
					Random random = new Random();

					output.append("Running Scenario 1...\n");

					//Sukuriamas kodavimo objektas bei atliekamas vektoriaus kodavimas
					Encoder encoder = new Encoder(m);
					String encodedData = encoder.encode(vectorText);

					//Sukuriamas kanalo objektas ir atliekama siuntimo kanalu simuliacija
					ChannelSimulator simulator = new ChannelSimulator(probability, random);
					String transmittedData = simulator.simulateChannel(encodedData);

					//Galimybė redaguoti persiųstą vektorių
					transmittedDataArea.setText(transmittedData);

					//Gaunami klaidingų bitų indeksai ir kiekis bei išvedami tarpiniai rezultatai
					List<Integer> errorPositions = simulator.getErrorPositions();
					output.append("Encoded Data: " + encodedData + "\n");
					output.append("Transmitted Data: " + transmittedData + "\n");
					output.append("Number of Errors: " + errorPositions.size() + "\n");
					output.append("Error Positions: " + errorPositions + "\n\n");
				} catch (Exception ex) {
					output.append("Error: " + ex.getMessage() + "\n");
				}
			}
		});

		// Antrojo mygtuko - dekodavimo veiksmai
		decodeButton.addActionListener(e -> {

			// Gaunami kanalu persiųsti duomenys ir m reikšmė
			String transmittedData = transmittedDataArea.getText();
			String mText = mField.getText();

			// Patikrinama ar vartotojo modifikuotas vektorius yra tinkamas
			if (!ValidationHelper.isValidBinaryVector(transmittedData, (int) Math.pow(2, Integer.parseInt(mText)))) {
				transmittedDataError.setText("Invalid: Must be a binary vector of length " + (int) Math.pow(2, Integer.parseInt(mText)));
				return;
			}

			transmittedDataError.setText("");

			// Dekodavimo veiksmai
			try {
				// Įvestų duomenų konvertavimas į reikiamus tipus
				int m = Integer.parseInt(mText);
				// Sukuriamas Hadamardo matricų masyvas, kviečiamas HadamardMatrixGenerator klasės metodas
				int[][][] hadamardMatrices = HadamardMatrixGenerator.generateHadamardMatrices(m);
				// Sukuriamas dekodavimo objektas
				Decoder decoder = new Decoder(m, hadamardMatrices);
				// Dekoduojamas kanalu persiųstas vektorius
				String decodedData = decoder.decode(transmittedData);
				output.append("Decoded Data: " + decodedData + "\n");
			} catch (Exception ex) {
				output.append("Error: " + ex.getMessage() + "\n");
			}
		});

		// Įvesties laukų ir t.t. formatavimas
		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(mLabel, gbc);
		gbc.gridx = 1;
		inputPanel.add(mField, gbc);
		gbc.gridx = 2;
		inputPanel.add(mError, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		inputPanel.add(vectorLabel, gbc);
		gbc.gridx = 1;
		inputPanel.add(vectorField, gbc);
		gbc.gridx = 2;
		inputPanel.add(vectorError, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		inputPanel.add(probabilityLabel, gbc);
		gbc.gridx = 1;
		inputPanel.add(probabilityField, gbc);
		gbc.gridx = 2;
		inputPanel.add(probabilityError, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		inputPanel.add(new JLabel("Modify Transmitted Data:"), gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		inputPanel.add(transmittedDataArea, gbc);
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		inputPanel.add(transmittedDataError, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		inputPanel.add(encodeTransmitButton, gbc);
		gbc.gridx = 1;
		inputPanel.add(decodeButton, gbc);

		panel.add(inputPanel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(backButton, BorderLayout.SOUTH);
		return panel;
	}

	// Antrasis scenarijus: Teksto perdavimas
	private static JPanel createScenario2Panel(JPanel cards) {
		JPanel panel = new JPanel(new BorderLayout());
		JTextArea output = new JTextArea(20, 50);
		output.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(output);

		JButton backButton = new JButton("Back to Main Menu");
		backButton.addActionListener(e -> showCard(cards, "MainMenu"));

		JPanel inputPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel mLabel = new JLabel("Enter m (positive integer >= 1):");
		JTextField mField = new JTextField();
		mField.setPreferredSize(new Dimension(200, 25));
		JLabel mError = new JLabel("");
		mError.setForeground(Color.RED);

		JLabel probabilityLabel = new JLabel("Enter error probability (0 <= p <= 1):");
		JTextField probabilityField = new JTextField();
		probabilityField.setPreferredSize(new Dimension(200, 25));
		JLabel probabilityError = new JLabel("");
		probabilityError.setForeground(Color.RED);

		JLabel textLabel = new JLabel("Enter text to transmit:");
		JTextArea textField = new JTextArea(5, 30);
		JScrollPane textScrollPane = new JScrollPane(textField);
		JLabel textError = new JLabel("");
		textError.setForeground(Color.RED);

		JButton sendWithoutEncodingButton = new JButton("Send Without Encoding");
		JButton sendWithEncodingButton = new JButton("Send With Encoding");


		// Kreipimasis į teksto perdavimo metodą processTextTransmission, kai paspaudžiamas siuntimo be kodavimo mygtukas
		sendWithoutEncodingButton.addActionListener(e ->
				processTextTransmission(output, mField, probabilityField, textField, mError, probabilityError, textError, false));

		// Kreipimasis į teksto perdavimo metodą processTextTransmission, kai paspaudžiamas siuntimo su kodavimo mygtukas
		sendWithEncodingButton.addActionListener(e ->
				processTextTransmission(output, mField, probabilityField, textField, mError, probabilityError, textError, true));

		// Įvesties laukų ir t.t. formatavimas
		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(mLabel, gbc);
		gbc.gridx = 1;
		inputPanel.add(mField, gbc);
		gbc.gridx = 2;
		inputPanel.add(mError, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		inputPanel.add(probabilityLabel, gbc);
		gbc.gridx = 1;
		inputPanel.add(probabilityField, gbc);
		gbc.gridx = 2;
		inputPanel.add(probabilityError, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		inputPanel.add(textLabel, gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		inputPanel.add(textScrollPane, gbc);
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		inputPanel.add(textError, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		inputPanel.add(sendWithoutEncodingButton, gbc);
		gbc.gridx = 1;
		inputPanel.add(sendWithEncodingButton, gbc);

		panel.add(inputPanel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(backButton, BorderLayout.SOUTH);
		return panel;
	}

	// Trečiasis scenarijus: Paveikslėlio perdavimas
	private static JPanel createScenario3Panel(JPanel cards) {
		JPanel panel = new JPanel(new BorderLayout());
		JTextArea output = new JTextArea(20, 50);
		output.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(output);

		JButton backButton = new JButton("Back to Main Menu");
		backButton.addActionListener(e -> showCard(cards, "MainMenu"));

    /* Eksperimentas
    JButton drawGraphButton = new JButton("Draw Graph");
    drawGraphButton.addActionListener(e -> ChartHelper.drawGraph(results.getMValues(), results.getDurations()));
    */

		JPanel inputPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel mLabel = new JLabel("Enter m (positive integer >= 1):");
		JTextField mField = new JTextField();
		mField.setPreferredSize(new Dimension(200, 25));
		JLabel mError = new JLabel("");
		mError.setForeground(Color.RED);

		JLabel probabilityLabel = new JLabel("Enter error probability (0 <= p <= 1):");
		JTextField probabilityField = new JTextField();
		probabilityField.setPreferredSize(new Dimension(200, 25));
		JLabel probabilityError = new JLabel("");
		probabilityError.setForeground(Color.RED);

		// BMP Paveikslėlio įkėlimo ir atvaizdavimo komponentai
		JLabel fileLabel = new JLabel("Select BMP Image:");
		JButton fileButton = new JButton("Upload Image");
		JLabel fileError = new JLabel("");
		fileError.setForeground(Color.RED);

		JLabel originalImageLabel = new JLabel();
		JLabel reconstructedImageWithoutEncodingLabel = new JLabel();  // Pridedama vieta paveiksleliui be kodavimo
		JLabel reconstructedImageWithEncodingLabel = new JLabel(); // Pridedama vieta paveiksleliui su kodavimu

		// BMP paveikslėlio įkėlimo, atvaizdavimo ir savotiškos validacijos veiksmai
		fileButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				try {
					BufferedImage img = ImageIO.read(file);
					if (img == null) throw new IllegalArgumentException("Not a valid BMP image.");
					originalImageLabel.setIcon(new ImageIcon(img.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
					fileError.setText(""); // Ištrinama klaidos žinutė jei įkėlimas pavyko
				} catch (Exception ex) {
					fileError.setText("Invalid image file!");
				}
			}
		});

		JButton sendWithoutEncodingButton = new JButton("Send Without Encoding");
		JButton sendWithEncodingButton = new JButton("Send With Encoding");

		// Kreipimasis į paveikslėlio perdavimo metodą processImageTransmission, kai paspaudžiamas siuntimo be kodavimo mygtukas
		sendWithoutEncodingButton.addActionListener(e ->
				processImageTransmission(output, originalImageLabel, reconstructedImageWithoutEncodingLabel, mField, probabilityField, mError, probabilityError, false));

		// Kreipimasis į paveikslėlio perdavimo metodą processImageTransmission, kai paspaudžiamas siuntimo su kodavimu mygtukas
		sendWithEncodingButton.addActionListener(e ->
				processImageTransmission(output, originalImageLabel, reconstructedImageWithEncodingLabel, mField, probabilityField, mError, probabilityError, true));

		// Įvesties laukų ir t.t. formatavimas
		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(mLabel, gbc);
		gbc.gridx = 1;
		inputPanel.add(mField, gbc);
		gbc.gridx = 2;
		inputPanel.add(mError, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		inputPanel.add(probabilityLabel, gbc);
		gbc.gridx = 1;
		inputPanel.add(probabilityField, gbc);
		gbc.gridx = 2;
		inputPanel.add(probabilityError, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		inputPanel.add(fileLabel, gbc);
		gbc.gridx = 1;
		inputPanel.add(fileButton, gbc);
		gbc.gridx = 2;
		inputPanel.add(fileError, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		inputPanel.add(originalImageLabel, gbc);
		gbc.gridx = 1;
		inputPanel.add(reconstructedImageWithoutEncodingLabel, gbc);  //Pridedama vieta paveiksleliui be kodavimo
		gbc.gridx = 2;
		inputPanel.add(reconstructedImageWithEncodingLabel, gbc); // Pridedama vieta paveiksleliui su kodavimu

		gbc.gridx = 0;
		gbc.gridy = 4;
		inputPanel.add(sendWithoutEncodingButton, gbc);
		gbc.gridx = 1;
		inputPanel.add(sendWithEncodingButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;

    /* Eksperimentas
    inputPanel.add(drawGraphButton, gbc);
    */

		panel.add(inputPanel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(backButton, BorderLayout.SOUTH);
		return panel;
	}

	// Antrojo scenarijaus teksto perdavimo metodas
	private static void processTextTransmission(JTextArea output, JTextField mField, JTextField probabilityField, JTextArea textField, JLabel mError, JLabel probabilityError, JLabel textError, boolean withEncoding) {
		// Sutikrinamos validacijos, ar įvestos laukų reikšmės yra tinkamos
		// @return true, jei visos reikšmės tinkamos, kitu atveju - false (ir tolimesni veiksmai nėra vykdomi)
		if (!validateInputs(mField, probabilityField, textField, mError, probabilityError, textError)) return;

		try {
			// Įvestų duomenų konvertavimas į reikiamus tipus
			int m = Integer.parseInt(mField.getText());
			double probability = Double.parseDouble(probabilityField.getText());
			// Sukuriamas atsitiktinių skaičių generatorius
			Random random = new Random();
			// Sukuriamas kanalo objektas
			ChannelSimulator simulator = new ChannelSimulator(probability, random);

			// Teksto skaidymas į vektorius naudojant TextTransformHelper klasės metodą ir jų saugojimas sąraše
			List<String> vectors = TextTransformHelper.splitTextIntoVectors(textField.getText(), m + 1);

			// Sukuriamas vektorių sąrašas, kuriame bus saugomi persiųsti vektoriai
			List<String> transmittedVectors = new ArrayList<>();

			// Jeigu pasirinkta su kodavimu, vykdomi kodavimo ir dekodavimo veiksmai
			if (withEncoding) {
				// Sukuriamas kodavimo objektas
				Encoder encoder = new Encoder(m);
				// Sukuriamas dekodavimo objektas
				Decoder decoder = new Decoder(m, HadamardMatrixGenerator.generateHadamardMatrices(m));
				for (String vector : vectors) {
					// Vektorius užkoduojamas, siunčiamas kanalu, dekoduojamas ir pridedamas prie persiųstų vektorių sąrašo
					transmittedVectors.add(decoder.decode(simulator.simulateChannel(encoder.encode(vector))));
				}
				output.append("With Encoding:\n");
			} else { // Jeigu pasirinkta be kodavimo, vykdoma kanalo simuliacija
				for (String vector : vectors) {
					transmittedVectors.add(simulator.simulateChannel(vector));
				}
				output.append("Without Encoding:\n");
			}

			// Teksto atkūrimas iš vektorių ir išvedimas
			output.append("Reconstructed Text: " + TextTransformHelper.reconstructTextFromVectors(transmittedVectors) + "\n");
		} catch (Exception ex) {
			output.append("Error: " + ex.getMessage() + "\n");
		}
	}

	private static final TransmissionResults results = new TransmissionResults();

	// Trečiojo scenarijaus paveikslėlio perdavimo metodas
	private static void processImageTransmission(JTextArea output, JLabel originalImageLabel, JLabel reconstructedImageLabel, JTextField mField, JTextField probabilityField, JLabel mError, JLabel probabilityError, boolean withEncoding) {
		// Sutikrinamos validacijos, ar įvestos laukų reikšmės yra tinkamos
		if (!validateInputs(mField, probabilityField, null, mError, probabilityError, null)) return;

		try {
			// Įvestų duomenų konvertavimas į reikiamus tipus
			int m = Integer.parseInt(mField.getText());
			double probability = Double.parseDouble(probabilityField.getText());
			// Įkeltas paveikslėlis konvertuojamas į BufferedImage objektą (saugomas bufferyje)
			BufferedImage img = ImageProcessingHelper.toBufferedImage(((ImageIcon) originalImageLabel.getIcon()).getImage());
			// Paveikslėlis skaidomas į vektorius ir saugomas sąraše
			List<String> vectors = ImageProcessingHelper.splitImageToVectors(img, m + 1);
			// Sukuriamas vektorių sąrašas, kuriame bus saugomi persiųsti vektoriai
			List<String> transmittedVectors = new ArrayList<>();
			// Sukuriamas atsitiktinių skaičių generatorius
			Random random = new Random();
			// Sukuriamas kanalo objektas
			ChannelSimulator simulator = new ChannelSimulator(probability, random);

			// Jeigu pasirinkta su kodavimu, vykdomi kodavimo, persiuntimo ir dekodavimo veiksmai
			if (withEncoding) {
				// Sukuriamas kodavimo objektas ir dekodavimo objektas
				Encoder encoder = new Encoder(m);
				Decoder decoder = new Decoder(m, HadamardMatrixGenerator.generateHadamardMatrices(m));

            /* Eksperimentas
            long startTime = System.currentTimeMillis();
            */

				for (String vector : vectors) {
					// Vektorius užkoduojamas, siunčiamas kanalu, dekoduojamas ir pridedamas prie persiųstų vektorių sąrašo
					transmittedVectors.add(decoder.decode(simulator.simulateChannel(encoder.encode(vector))));
				}

            /* Eksperimentas
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            results.addResult(m, duration);
            */
			} else {
				for (String vector : vectors) {
					// Vektorius siunčiamas kanalu
					transmittedVectors.add(simulator.simulateChannel(vector));
				}
			}

			// Paveikslėlio atkūrimas iš vektorių ir išvedimas
			BufferedImage reconstructedImg = ImageProcessingHelper.reconstructImageFromVectors(transmittedVectors, img.getWidth(), img.getHeight());
			reconstructedImageLabel.setIcon(new ImageIcon(reconstructedImg.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));

			output.append("Image transmission complete.\n");
		} catch (Exception e) {
			output.append("Error: " + e.getMessage() + "\n");
		}
	}

	// Validacija įvesties laukų
	private static boolean validateInputs(JTextField mField, JTextField probabilityField, JTextArea textField, JLabel mError, JLabel probabilityError, JLabel textError) {
		boolean valid = true;

		if (!ValidationHelper.isPositiveInteger(mField.getText())) {
			mError.setText("Invalid: Must be a positive integer >= 1");
			valid = false;
		} else {
			mError.setText("");
		}

		if (!ValidationHelper.isProbability(probabilityField.getText())) {
			probabilityError.setText("Invalid: Must be a real number between 0 and 1, format x.x");
			valid = false;
		} else {
			probabilityError.setText("");
		}

		if (textField != null && textField.getText().isEmpty()) {
			textError.setText("Text cannot be empty");
			valid = false;
		} else if (textError != null) {
			textError.setText("");
		}

		return valid;
	}

	// Kortelės rodymas
	private static void showCard(JPanel cards, String cardName) {
		CardLayout cl = (CardLayout) cards.getLayout();
		cl.show(cards, cardName);
	}
}
