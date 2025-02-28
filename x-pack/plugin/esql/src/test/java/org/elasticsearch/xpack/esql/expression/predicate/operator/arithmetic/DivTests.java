/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.esql.expression.predicate.operator.arithmetic;

import com.carrotsearch.randomizedtesting.annotations.Name;
import com.carrotsearch.randomizedtesting.annotations.ParametersFactory;

import org.elasticsearch.xpack.ql.expression.Expression;
import org.elasticsearch.xpack.ql.expression.predicate.operator.arithmetic.Div;
import org.elasticsearch.xpack.ql.tree.Source;
import org.elasticsearch.xpack.ql.type.DataTypes;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Supplier;

import static org.elasticsearch.xpack.ql.util.NumericUtils.asLongUnsigned;
import static org.elasticsearch.xpack.ql.util.NumericUtils.unsignedLongAsBigInteger;
import static org.hamcrest.Matchers.equalTo;

public class DivTests extends AbstractArithmeticTestCase {
    public DivTests(@Name("TestCase") Supplier<TestCase> testCaseSupplier) {
        this.testCase = testCaseSupplier.get();
    }

    @ParametersFactory
    public static Iterable<Object[]> parameters() {
        return parameterSuppliersFromTypedData(List.of(new TestCaseSupplier("Int / Int", () -> {
            int lhs = randomInt();
            int rhs;
            do {
                rhs = randomInt();
            } while (rhs == 0);
            return new TestCase(
                List.of(new TypedData(lhs, DataTypes.INTEGER, "lhs"), new TypedData(rhs, DataTypes.INTEGER, "rhs")),
                "DivIntsEvaluator[lhs=Attribute[channel=0], rhs=Attribute[channel=1]]",
                DataTypes.INTEGER,
                equalTo(lhs / rhs)
            );
        }), new TestCaseSupplier("Long / Long", () -> {
            long lhs = randomLong();
            long rhs;
            do {
                rhs = randomLong();
            } while (rhs == 0);
            return new TestCase(
                List.of(new TypedData(lhs, DataTypes.LONG, "lhs"), new TypedData(rhs, DataTypes.LONG, "rhs")),
                "DivLongsEvaluator[lhs=Attribute[channel=0], rhs=Attribute[channel=1]]",
                DataTypes.LONG,
                equalTo(lhs / rhs)
            );
        }), new TestCaseSupplier("Double / Double", () -> {
            double lhs = randomDouble();
            double rhs;
            do {
                rhs = randomDouble();
            } while (rhs == 0);
            return new TestCase(
                List.of(new TypedData(lhs, DataTypes.DOUBLE, "lhs"), new TypedData(rhs, DataTypes.DOUBLE, "rhs")),
                "DivDoublesEvaluator[lhs=Attribute[channel=0], rhs=Attribute[channel=1]]",
                DataTypes.DOUBLE,
                equalTo(lhs / rhs)
            );
        })/*, new TestCaseSupplier("ULong / ULong", () -> {
            // Ensure we don't have an overflow
            long lhs = randomLong();
            long rhs;
            do {
                rhs = randomLong();
            } while (rhs == 0);
            BigInteger lhsBI = unsignedLongAsBigInteger(lhs);
            BigInteger rhsBI = unsignedLongAsBigInteger(rhs);
            return new TestCase(
                Source.EMPTY,
                List.of(new TypedData(lhs, DataTypes.UNSIGNED_LONG, "lhs"), new TypedData(rhs, DataTypes.UNSIGNED_LONG, "rhs")),
                "DivUnsignedLongsEvaluator[lhs=Attribute[channel=0], rhs=Attribute[channel=1]]",
                equalTo(asLongUnsigned(lhsBI.divide(rhsBI).longValue()))
            );
          })
          */
        ));
    }

    @Override
    protected boolean rhsOk(Object o) {
        if (o instanceof Number n) {
            return n.doubleValue() != 0;
        }
        return true;
    }

    @Override
    protected Div build(Source source, Expression lhs, Expression rhs) {
        return new Div(source, lhs, rhs);
    }

    @Override
    protected double expectedValue(double lhs, double rhs) {
        return lhs / rhs;
    }

    @Override
    protected int expectedValue(int lhs, int rhs) {
        return lhs / rhs;
    }

    @Override
    protected long expectedValue(long lhs, long rhs) {
        return lhs / rhs;
    }

    @Override
    protected long expectedUnsignedLongValue(long lhs, long rhs) {
        BigInteger lhsBI = unsignedLongAsBigInteger(lhs);
        BigInteger rhsBI = unsignedLongAsBigInteger(rhs);
        return asLongUnsigned(lhsBI.divide(rhsBI).longValue());
    }
}
