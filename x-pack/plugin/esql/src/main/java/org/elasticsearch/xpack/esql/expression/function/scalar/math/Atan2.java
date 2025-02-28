/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.esql.expression.function.scalar.math;

import org.elasticsearch.compute.ann.Evaluator;
import org.elasticsearch.compute.operator.EvalOperator;
import org.elasticsearch.xpack.esql.planner.Mappable;
import org.elasticsearch.xpack.ql.expression.Expression;
import org.elasticsearch.xpack.ql.expression.Expressions;
import org.elasticsearch.xpack.ql.expression.TypeResolutions;
import org.elasticsearch.xpack.ql.expression.function.scalar.ScalarFunction;
import org.elasticsearch.xpack.ql.expression.gen.script.ScriptTemplate;
import org.elasticsearch.xpack.ql.tree.NodeInfo;
import org.elasticsearch.xpack.ql.tree.Source;
import org.elasticsearch.xpack.ql.type.DataType;
import org.elasticsearch.xpack.ql.type.DataTypes;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.elasticsearch.xpack.ql.expression.TypeResolutions.isNumeric;

/**
 * Inverse cosine trigonometric function.
 */
public class Atan2 extends ScalarFunction implements Mappable {
    private final Expression y;
    private final Expression x;

    public Atan2(Source source, Expression y, Expression x) {
        super(source, List.of(y, x));
        this.y = y;
        this.x = x;
    }

    @Override
    public Expression replaceChildren(List<Expression> newChildren) {
        return new Atan2(source(), newChildren.get(0), newChildren.get(1));
    }

    @Override
    protected NodeInfo<? extends Expression> info() {
        return NodeInfo.create(this, Atan2::new, y, x);
    }

    @Evaluator
    static double process(double y, double x) {
        return Math.atan2(y, x);
    }

    @Override
    public DataType dataType() {
        return DataTypes.DOUBLE;
    }

    @Override
    protected TypeResolution resolveType() {
        if (childrenResolved() == false) {
            return new TypeResolution("Unresolved children");
        }

        TypeResolution resolution = isNumeric(y, sourceText(), TypeResolutions.ParamOrdinal.FIRST);
        if (resolution.unresolved()) {
            return resolution;
        }
        return isNumeric(x, sourceText(), TypeResolutions.ParamOrdinal.SECOND);
    }

    @Override
    public boolean foldable() {
        return Expressions.foldable(children());
    }

    @Override
    public Supplier<EvalOperator.ExpressionEvaluator> toEvaluator(
        Function<Expression, Supplier<EvalOperator.ExpressionEvaluator>> toEvaluator
    ) {
        Supplier<EvalOperator.ExpressionEvaluator> yEval = Cast.cast(y.dataType(), DataTypes.DOUBLE, toEvaluator.apply(y));
        Supplier<EvalOperator.ExpressionEvaluator> xEval = Cast.cast(x.dataType(), DataTypes.DOUBLE, toEvaluator.apply(x));
        return () -> new Atan2Evaluator(yEval.get(), xEval.get());
    }

    @Override
    public Object fold() {
        return Mappable.super.fold();
    }

    @Override
    public ScriptTemplate asScript() {
        throw new UnsupportedOperationException();
    }

    public Expression y() {
        return y;
    }

    public Expression x() {
        return x;
    }
}
