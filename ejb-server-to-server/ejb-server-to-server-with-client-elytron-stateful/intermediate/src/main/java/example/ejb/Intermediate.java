package example.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

/**
 * @author <a href="mailto:mjurc@redhat.com">Michal Jurc</a>
 */
@Stateless
public class Intermediate implements WhoAmIBeanRemote {

    @Resource
    private SessionContext ctx;

    @EJB(lookup = "ejb:/server-side/WhoAmIBean!example.ejb.WhoAmIBeanRemote?stateful")
    private WhoAmIBeanRemote serverSideWhoAmI;

    public String whoAmI() {
        try {
            final String user = ctx.getCallerPrincipal().getName();
            System.out.println("Intermediate Stateless WhoAmIBean.whoAmI called as " + user);
            return serverSideWhoAmI.whoAmI();
        } catch(Exception e) {
            throw new RuntimeException("Intermediary server was unable to call the target server");
        }
    }

}
