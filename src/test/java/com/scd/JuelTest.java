package com.scd;


import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import org.junit.Test;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

/**
 * @author James
 */
public class JuelTest {

    @Test
    public void testJuel() throws NoSuchMethodException {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        context.setFunction("math", "max", Math.class.getMethod("max",
                int.class, int.class));
        context.setVariable("foo", factory.createValueExpression(0, int.class));
        context.setVariable("bar", factory.createValueExpression(1, int.class));
        ValueExpression valueExpression = factory.createValueExpression(context, "${math:max(foo,bar)}", int.class);
        System.out.println(valueExpression.getValue(context));
    }
}
