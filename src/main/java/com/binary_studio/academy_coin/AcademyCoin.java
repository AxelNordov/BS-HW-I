package com.binary_studio.academy_coin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AcademyCoin {

    private AcademyCoin() {
    }

    public static int maxProfit(Stream<Integer> prices) {
        List<Integer> pricesList = prices.collect(Collectors.toList());
        if (pricesList.size() == 0) {
            return 0;
        }
        int maxResult = 0;
        for (int i = 0; i < pricesList.size() - 1; i++) {
            for (int j = i + 1; j < pricesList.size(); j++) {
                maxResult = Math.max(maxResult,
                        pricesList.get(j) - pricesList.get(i) +
                                maxProfit(pricesList.subList(j + 1, pricesList.size()).stream()));
            }
        }
        return maxResult;
    }

}
