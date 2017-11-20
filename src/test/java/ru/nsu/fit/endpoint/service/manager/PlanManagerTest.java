package ru.nsu.fit.endpoint.service.manager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import ru.nsu.fit.endpoint.service.database.DBService;
import ru.nsu.fit.endpoint.service.database.data.Plan;

import java.util.UUID;

public class PlanManagerTest {
    private DBService dbService;
    private Logger logger;
    private PlanManager planManager;

    private Plan planBeforeAdding;
    private Plan planAfterAdding;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        dbService = Mockito.mock(DBService.class);

        logger = Mockito.mock(Logger.class);

        planBeforeAdding = new Plan();
        planBeforeAdding.setName("superplan");
        planBeforeAdding.setFee(1000);
        planBeforeAdding.setDetails("ochen good plan");

        planAfterAdding = new Plan();

        Mockito.when(dbService.createPlan(planBeforeAdding)).thenAnswer((Answer<Plan>) invocationOnMock -> {
            Plan plan = invocationOnMock.getArgumentAt(0, Plan.class);
            planAfterAdding.setName(plan.getName());
            planAfterAdding.setDetails(plan.getDetails());
            planAfterAdding.setFee(plan.getFee());
            return planAfterAdding;
        });

        planManager = new PlanManager(dbService, logger);
    }

    @Test
    public void testCreateNullPlan() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Plan must not be null");

        planManager.createPlan(null);
    }

    @Test
    public void testCreation() {
        planBeforeAdding.setName("superplan");
        planBeforeAdding.setDetails("this is a really good plan");
        planBeforeAdding.setFee(6);

        Plan returned = planManager.createPlan(planBeforeAdding);

        Assert.assertEquals(planBeforeAdding.getName(), returned.getName());
        Assert.assertEquals(planBeforeAdding.getDetails(), returned.getDetails());
        Assert.assertEquals(planBeforeAdding.getFee(), returned.getFee());
        Mockito.verify(dbService).createPlan(Mockito.any(Plan.class));
    }

}
