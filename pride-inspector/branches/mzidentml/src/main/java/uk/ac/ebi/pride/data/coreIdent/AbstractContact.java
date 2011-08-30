package uk.ac.ebi.pride.data.coreIdent;

/**
 * This class is just to organize the code, the ConstantRole class have to kind of instance,
 * one of then is Organization and the Other one if the person both objects extend the IdentifiableParamGroup
 * in order to organize the code we create a new abstract class named AbstractContact to separate the Contacts types
 * from the IdentifiableParamGroup.
 *
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 19/08/11
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public abstract  class AbstractContact extends IdentifiableParamGroup{

    /**
     * To create an instance just with Id and Name
     * @param id
     * @param name
     */
    public AbstractContact(Comparable id,
                           String name) {
        super(id, name);
    }

    /**
     * To create an Instance with Id, name and ParamGroup
     * @param params
     * @param id
     * @param name
     */
    protected AbstractContact(ParamGroup params,
                              Comparable id,
                              String name) {
        super(params, id, name);
    }
}
