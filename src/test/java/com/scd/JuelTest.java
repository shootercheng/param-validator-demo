package com.scd;


import com.alibaba.fastjson.JSON;
import com.scd.mvctest.business.model.DataVO;
import com.scd.mvctest.business.model.ParamVO;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import org.junit.Test;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testJson() {
        String name = "scd";
        List<ParamVO> paramVOList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ParamVO paramVO = new ParamVO();
            paramVO.setId((long) i);
            paramVO.setName(name);
            paramVOList.add(paramVO);
            List<DataVO> dataVOList = new ArrayList<>();
            DataVO dataVO = new DataVO();
            dataVO.setId(i);
            if (i != 2) {
                dataVOList.add(dataVO);
            }
            paramVO.setList(dataVOList);
        }
        String json = JSON.toJSONString(paramVOList);
        System.out.println(json);
    }
}
