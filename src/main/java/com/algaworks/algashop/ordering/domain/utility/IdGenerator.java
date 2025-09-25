package com.algaworks.algashop.ordering.domain.utility;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;
import io.hypersistence.tsid.TSID;

import java.util.UUID;

public class IdGenerator {

    private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator
            = Generators.timeBasedEpochRandomGenerator();

    private static final TSID.Factory tsidFactory = TSID.Factory.INSTANCE;

    private IdGenerator() {}

    public static UUID generateTimeBasedUUID() {
        return timeBasedEpochRandomGenerator.generate();
    }

    /*
     * ao subir em produção precsa passar as variáveis abaixo
     * TSID_NODE (qual é a instancia desse microsservico que temos se é a 1, começa no 0. ex. 0)
     * TSID_NODE_COUNT (quantidade de instancias desse microsservico que temos, ex. 3)
     */
    public static TSID generateTSID() {
        return tsidFactory.generate();
    }
}
