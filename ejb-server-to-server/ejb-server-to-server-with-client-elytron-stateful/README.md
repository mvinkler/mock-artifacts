## Invoking EJBs in a cluster-to-cluster scenario using Elytron on the client side

If you do not run the servers on separate physical machines, JBOSS-LOCAL-USER auth is used and the scenario works.

It is necessary to use separate physical machines, standalone-ha.xml profile + stateful target bean to reproduce the issue.

### 1. Prepare server-side EAP
1. Add the application user:
```
bin/add-user.sh -a -g users -u admin -p admin123+
```
2. !!! Run EAP with standalone-ha.xml profile on separate physical machine !!!
3. Build and deploy the `server` project

### 2. Prepare client-side EAP
1. Add the application user:
```
bin/add-user.sh -a -g users -u joe -p joeIsAwesome2013!
```
2. !!! Run EAP with standalone-ha.xml profile on separate physical machine !!! with property `-Dremote.ejb.host=HOSTNAME_OF_REMOTE_SERVER` (where `HOSTNAME_OF_REMOTE_SERVER` is the address where server-side EAP is available)
3. Configure the things needed for the EJB client connection:
```
/socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=remote-ejb:add(host=${remote.ejb.host}, port=8080)
/subsystem=elytron/authentication-configuration=admin-cfg:add(sasl-mechanism-selector="DIGEST-MD5", credential-reference={clear-text="admin123+"}, authentication-name=admin, realm=ApplicationRealm)
/subsystem=elytron/authentication-context=admin-ctx:add(match-rules=[{authentication-configuration=admin-cfg}])
/subsystem=remoting/remote-outbound-connection=remote-ejb-connection:add(authentication-context=admin-ctx, outbound-socket-binding-ref=remote-ejb)
```

3. Build and deploy the `intermediate` project

### 3. Run the example
1. Build and run the `client` project: `mvn clean install exec:exec -Dintermediate.ejb.host=HOSTNAME_OF_INTERMEDIATE_SERVER` (where `HOSTNAME_OF_INTERMEDIATE_SERVER` is EAP with `intermediate` deployment).
