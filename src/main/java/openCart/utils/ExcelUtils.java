package openCart.utils;

import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import net.serenitybdd.screenplay.Actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {
    private String filePath;
    private Fillo fillo;
    private Connection connection;

    public ExcelUtils(String filePath) {
        this.filePath = filePath;
        this.fillo = new Fillo();
    }

    /**
     * Método para leer datos de Excel
     * @param sheetName
     * @param query
     * @return
     */
    public void readData(String sheetName, String query, Actor actor) {
        List<Map<String, String>> results = new ArrayList<>();
        try {
            connection = fillo.getConnection(filePath);
            String selectQuery = "SELECT * FROM " + sheetName + " WHERE " + query;
            Recordset recordset = connection.executeQuery(selectQuery);

            while (recordset.next()) {
                Map<String, String> rowMap = new HashMap<>();

                // Obtén todas las columnas y sus valores
                for (String columnName : recordset.getFieldNames()) {
                    rowMap.put(columnName, recordset.getField(columnName));
                }

                // Agrega el Map a la lista
                results.add(rowMap);
                actor.remember(sheetName,rowMap);
            }
            recordset.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Método para escribir datos en Excel
     * @param sheetName
     * @param columnName
     * @param condition
     * @param value
     */
    public void writeData(String sheetName, String columnName, String condition, String value) {
        try {
            connection = fillo.getConnection(filePath);
            String updateQuery = String.format("UPDATE %s SET %s='%s' WHERE %s", sheetName, columnName, value, condition);
            connection.executeUpdate(updateQuery);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
