package core.basesyntax;

import core.basesyntax.model.FruitTransaction;
import core.basesyntax.service.CsvFileReaderService;
import core.basesyntax.service.CsvFileWriterService;
import core.basesyntax.service.FruitShopService;
import core.basesyntax.service.ReportCreatorService;
import core.basesyntax.service.TransactionParseService;
import core.basesyntax.service.impl.CsvFileReaderImpl;
import core.basesyntax.service.impl.CsvFileWriterServiceImpl;
import core.basesyntax.service.impl.FruitShopServiceImpl;
import core.basesyntax.service.impl.ReportCreatorServiceImpl;
import core.basesyntax.service.impl.TransactionParseServiceImpl;
import core.basesyntax.strategy.OperationHandler;
import core.basesyntax.strategy.impl.BalanceOperationHandler;
import core.basesyntax.strategy.impl.OperationStrategyImpl;
import core.basesyntax.strategy.impl.PurchaseOperationHandler;
import core.basesyntax.strategy.impl.ReturnOperationHandler;
import core.basesyntax.strategy.impl.SupplyOperationHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final String INPUT_FILE = "src/main/resources/input.csv";
    private static final String OUTPUT_FILE = "src/main/resources/output.csv";

    public static void main(String[] args) {
        Map<FruitTransaction.Operation, OperationHandler> operationHandlerMap = new HashMap<>();
        operationHandlerMap.put(FruitTransaction.Operation.BALANCE, new BalanceOperationHandler());
        operationHandlerMap.put(FruitTransaction.Operation.SUPPLY, new SupplyOperationHandler());
        operationHandlerMap.put(FruitTransaction.Operation.PURCHASE, new PurchaseOperationHandler());
        operationHandlerMap.put(FruitTransaction.Operation.RETURN, new ReturnOperationHandler());

        CsvFileReaderService readerService = new CsvFileReaderImpl();
        TransactionParseService transactionParseService = new TransactionParseServiceImpl();
        List<FruitTransaction> parsed = transactionParseService.parse(
                readerService.readFromFile(INPUT_FILE));
        FruitShopService fruitShopService = new FruitShopServiceImpl(
                new OperationStrategyImpl(operationHandlerMap));
        Map<String, Integer> preparedMap = fruitShopService.process(parsed);

        ReportCreatorService reportMakerService = new ReportCreatorServiceImpl();
        String preparedReport = reportMakerService.createReport(preparedMap);

        CsvFileWriterService writerService = new CsvFileWriterServiceImpl();
        writerService.writeToFile(OUTPUT_FILE, preparedReport);
    }
}
