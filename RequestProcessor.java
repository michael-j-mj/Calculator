import java.io.*;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;
//additional user import
import java.text.DecimalFormat;

//
class RequestProcessor implements Runnable {
	private Socket socket = null;
	private OutputStream os = null;
	private BufferedReader in = null;
	private DataInputStream dis = null;
	private String msgToClient = "HTTP/1.1 200 OK\n" + "Server: HTTP server/0.1\n"
			+ "Access-Control-Allow-Origin: *\n\n";
	private JSONObject jsonObject = new JSONObject();

	public RequestProcessor(Socket Socket) {
		super();
		try {
			socket = Socket;
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// TODO separate each fragment into own method String[] readInput,double
	// evaluate,void writeOuput
	// TODO import big decimal & regex for more accurate decimal handling and
	// shorter code.
	public void run() {
		// write your code here
		double leftOperand = 0;
		double rightOperand = 0;
		char operation = ' ';
		double result = 0;
		String errorMsg = null;
		String inputMsg = "";
		// Read Input
		//////////////////
		try {

			inputMsg = in.readLine();

		} catch (IOException e) {
			System.out.println("Input Parse Failure: " + e.getMessage());
			try {
				socket.close();
			} catch (IOException e1) {
				System.out.println("Socket failure: " + e1.getMessage());
			}
			return;
		}
		// assume input values are located between / and (H)ttp
		if (inputMsg.indexOf('/') == -1 || inputMsg.indexOf('H') == -1
				|| inputMsg.indexOf('H') < inputMsg.indexOf('/')) {
			try {
				socket.close();
			} catch (IOException e1) {
				System.out.println("Input Parse Failure: " + e1.getMessage());
			}
			return;
		}

		inputMsg = inputMsg.substring(inputMsg.indexOf('/'), inputMsg.indexOf('H') - 1);
		String[] inputs = inputMsg.split("&"); // Separate into each arguments

		if (inputs.length == 3) {
			for (int i = 0; i < inputs.length; i++) { // parse value after =
				inputs[i] = inputs[i].substring(inputs[i].indexOf('=') + 1, inputs[i].length());
				if (inputs[i].length() <= 0) {
					errorMsg = "ERROR:INVALID INPUT ARGUMENT";
				}
			}

			if (errorMsg == null) {
				try {
					leftOperand = Double.parseDouble(inputs[0]);
					rightOperand = Double.parseDouble(inputs[1]);
					if (inputs[2].length() == 1) {
						operation = (inputs[2]).charAt(0);
					} else {
						errorMsg = "ERROR:INVALID INPUT ARGUMENT";
					}

				} catch (NumberFormatException e) {
					errorMsg = "ERROR:INVALID INPUT ARGUMENT";
					System.out.println("Input Value Failure: " + e.getMessage());
				}
			}
		} else {
			errorMsg = "ERROR:INVALID INPUT ARGUMENT";
		}

		// operations
		////////////////////////////////////
		if (errorMsg == null) {
			switch (operation) {
			case '+':
				result = (leftOperand + rightOperand);
				break;
			case '-':
				result = (leftOperand - rightOperand);
				break;
			case '*':
				result = (leftOperand * rightOperand);
				break;
			case '/':
				if (rightOperand == 0) {
					errorMsg = "ERROR:DIVIDE BY ZERO";
				} else {
					result = (leftOperand / rightOperand);
				}
				break;
			case '%':
				if (rightOperand == 0) {
					errorMsg = "ERROR:DIVIDE BY ZERO";
				} else {
					result = (leftOperand % rightOperand);
				}
				break;
			default:
				errorMsg = "ERROR:OPERATION NOT FOUND";
				break;
			}
		}

		// setJSon
		///////////////////////////

		try {
			if (errorMsg != "ERROR:INVALID INPUT ARGUMENT") {
				jsonObject.put("Expression",
						formatNumber(leftOperand) + " " + inputs[2] + " " + formatNumber(rightOperand));
			}
			if (errorMsg != null) {
				jsonObject.put("Result", errorMsg);
			} else {

				jsonObject.put("Result", formatNumber(result));
			}

		} catch (JSONException e2) {

			e2.printStackTrace();
		}

		// end of your code
		// Added try/catch block to get rid eclipse warnings.
		String response = msgToClient + jsonObject.toString();
		try { 
			os.write(response.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// importing text.DecimalFormat
	// as wasn't sure when writing this if extra import were allowed, left old
	// return statement below
	
	// convert a number to string based on size or decimal error correction
	//insert commas with decimalformat for middle sized number(ex: 10,000)
	// Input: double Number: the value to be converted
	// output: String of number, simple conversion, error rounding or scientific format
	private static String formatNumber(double number) {
		int numMaxLength = 10; // max char length of number
		DecimalFormat formatter = new DecimalFormat("#,###.####");
		String numberString = String.valueOf(number);

		if (numberString.length() > numMaxLength) { // possible redundant as double converts in very large or small
			return String.format("%6.3e", number);
		} else if (number == (long) number) { // indicates no decimal values & removes .0 from string
			return formatter.format((long) number);
			// return String.format("%d", (long) number);
		} else {
			// handle double point error for decimal
			double decimalPoint = Math.pow(10, 6);
			number = (double) Math.round(number * decimalPoint) / decimalPoint;
			return String.valueOf(number);
			// return formatter.format(number);
		}

	}
}
