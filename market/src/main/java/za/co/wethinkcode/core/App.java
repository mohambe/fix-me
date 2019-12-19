package za.co.wethinkcode.core;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.NoSuchElementException;
import za.co.wethinkcode.model.executor.MarketExecutor;
import za.co.wethinkcode.model.factory.InstrumentFactory;
import za.co.wethinkcode.model.market.MarketTower;
import za.co.wethinkcode.model.product.Product;

/**
 * Market App!
 *
 */

public class App {
	private static List<String> fileContents = new ArrayList<String>();
	public static List<Product>  products = new ArrayList<Product>();
	public static List<String>  logMessage = new ArrayList<String>();

	static MarketExecutor market;

	public static void main(String[] args) {
		MarketTower tower = new MarketTower();
		try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
			marketStockReader(reader, args[0]);
			parseStock();
			for (Product product: products) {
				product.registerMarket(tower);
			}
			market = new MarketExecutor();
			market.run();
		} catch (StockException ex) {
			System.out.print(ex.getMessage());
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		catch (NoSuchElementException e) {
			System.out.println(market._td.viewMessage("Router Disconnection Detected!","error"));
		}
    }

    private static void marketStockReader(BufferedReader reader, String fileName) throws IOException, NoSuchElementException {
	    String line = null;
	    while ((line = reader.readLine()) != null) {
		    fileContents.add(line);
	    }
	}

    private static void parseStock() throws StockException {
	    String[] InstrumentDetails = null; // product type, name, price and qty
		int lineNumberInFile = 0;
		
	    for(lineNumberInFile = 0; lineNumberInFile < fileContents.size(); lineNumberInFile += 1) {
		    InstrumentDetails = fileContents.get(lineNumberInFile).split(" ");
		    // Expected Format 				
				//  PRODUCT_TYPE NAME PRICE QTY

		    if (InstrumentDetails.length != 4) throw new StockException("Stock not properly formatted", fileContents.get(lineNumberInFile));
		    // PRICE and QTY should atleast be greater than 0
		    if (Integer.valueOf(InstrumentDetails[2]) < 0 || Integer.valueOf(InstrumentDetails[3]) < 0)
			    throw new StockException("Product price or quantity cannot be 0.", fileContents.get(lineNumberInFile));
		    // Depending on what the markets are selling
		    if (InstrumentDetails[0].equals("Bread") || InstrumentDetails[0].equals("Egg") || InstrumentDetails[0].equals("Bacon")) {// function to check if the product is part of any market
				try {
					products.add(InstrumentFactory.newInstrument(InstrumentDetails[0],
																InstrumentDetails[1],
																Integer.valueOf(InstrumentDetails[2]),
																Integer.valueOf(InstrumentDetails[3])));
				} catch(NumberFormatException e) {
					throw new StockException("Expected a number", fileContents.get(lineNumberInFile), e);
				}
				catch (NoSuchElementException e) {
					System.out.println(market._td.viewMessage("Router Disconnection Detected!","error"));
				}
			} else
				throw new StockException(InstrumentDetails[lineNumberInFile]+" is not recognized as a Product on line", "'"+lineNumberInFile+"'");
	    }
    }
}
