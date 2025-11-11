package openCart.utils;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;

@Slf4j
public class ExcelReadWrite {
    public static void testReadWriteExcel(String data, String idCaso, List<Map<String, Object>> datosPrueba, Actor actor) {

        Map<String, Object> firstMap = datosPrueba.get(0);
        String valor;
        String filePath = "src/test/resources/data/"+data+".xlsx";
        log.info("Lee archivo " + filePath);
        ExcelUtils excelUtils = new ExcelUtils(filePath);

        // Iterar sobre los valores del Map
        for (Object value : firstMap.values()) {
            valor = value.toString();
            System.out.println(value.toString());
            excelUtils.readData(valor, "idCaso="+idCaso,actor);
            log.info("ID caso " + idCaso);
        }
    }
}
