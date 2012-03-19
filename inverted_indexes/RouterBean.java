package climspace.input;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import climspace.current.DataProviderRemote;
import climspace.current.StationData;
import climspace.historical.WritingMongo;
import climspace.historical.WritingMongoEJB;


@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/router"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "10") })
// note if we add max sessions it is a jboss only property
public class RouterBean implements MessageListener {

	WritingMongo wf = new WritingMongoEJB();

	public RouterBean() {

	}

	/**
	 * messages forwarded to this method
	 * 
	 * @param msg
	 */
	public void onMessage(Message msg) {
	//	System.out.println("In RouterBean");
		
		Map json = new LinkedHashMap();
		ObjectMessage om = (ObjectMessage) msg;
		String jkey,jval;
		Class fileDataObjClass = FileDataObj.class;
		Class stationDataClass = StationData.class;
		FileDataObj msgObj = new FileDataObj();
		try {
			json = (Map) om.getObject();
			System.out.println(json);
			 Set keyArray = json.keySet();
			 Iterator iter = keyArray.iterator();
			 
			 Method fileDataObjMeth = null, statDataObjMeth = null;
			 SimpleDateFormat df =  new SimpleDateFormat("yyyyMMdd/HHmm");
			 Properties properties = new Properties();
			 properties.put(Context.PROVIDER_URL, "jnp://localhost:1099");
			 properties.put(Context.INITIAL_CONTEXT_FACTORY,"org.jnp.interfaces.NamingContextFactory");
			 properties.put(Context.URL_PKG_PREFIXES,"org.jboss.naming:org.jnp.interfaces");
			 Context ctx = new InitialContext(properties);
			 Object ref = ctx.lookup("DataProvider/remote");
			 DataProviderRemote ist = (DataProviderRemote) PortableRemoteObject.narrow(ref, DataProviderRemote.class);
			// System.out.println(json+":"+ist.checkStation(msgObj.getstnid().trim()));
			 if(json.get("type").equals("data"))
			 {
				 for(int i=0;i<keyArray.size();i++)
				 {
					 jkey = (String) iter.next();
					 jval = (String) json.get(jkey); 
					 if (jkey.equals("recDate"))
					 {
						 Date dt = null;
						 try {
							dt = df.parse(jval.trim());
							msgObj.setrecDate(dt);
						} catch (ParseException e) {
							System.out.println("Date format not proper");
							continue;
						}
					 }
					 //check this part for setting value...i ve used a json obj..set+"json key woule be the name of the func
					 //and the json value would be the value to be written to DB
					 else
					 {
						 try {
							 fileDataObjMeth = fileDataObjClass.getMethod("set"+(jkey.trim()), new Class[]{String.class});
							 fileDataObjMeth.invoke(msgObj, jval);
						 } catch (NoSuchMethodException e) {
							 System.out.println("Ignore value not present");
							 continue;
						 }
					 }
				 }
				 if(ist.checkStation(msgObj.getstnid().trim()))
				 {
					 msgObj.setSLAT(((msgObj.getSLAT()==null)||(msgObj.getSLAT().equals("")))?"N/A":msgObj.getSLAT());
					 msgObj.setSLON(((msgObj.getSLON()==null)||(msgObj.getSLON().equals("")))?"N/A":msgObj.getSLON());
					 msgObj.setALTI(((msgObj.getALTI()==null)||(msgObj.getALTI().equals("")))?"N/A":msgObj.getALTI());
					 msgObj.setDRCT(((msgObj.getDRCT()==null)||(msgObj.getDRCT().equals("")))?"N/A":msgObj.getDRCT());
					 msgObj.setDWPF(((msgObj.getDWPF()==null)||(msgObj.getDWPF().equals("")))?"N/A":msgObj.getDWPF());
					 msgObj.setGUST(((msgObj.getGUST()==null)||(msgObj.getGUST().equals("")))?"N/A":msgObj.getGUST());
					 msgObj.setMNET(((msgObj.getMNET()==null)||(msgObj.getMNET().equals("")))?"N/A":msgObj.getMNET());
					 msgObj.setP24I(((msgObj.getP24I()==null)||(msgObj.getP24I().equals("")))?"N/A":msgObj.getP24I());
					 msgObj.setPMSL(((msgObj.getPMSL()==null)||(msgObj.getPMSL().equals("")))?"N/A":msgObj.getPMSL());
					 msgObj.setRELH(((msgObj.getRELH()==null)||(msgObj.getRELH().equals("")))?"N/A":msgObj.getRELH());
					 msgObj.setSELV(((msgObj.getSELV()==null)||(msgObj.getSELV().equals("")))?"N/A":msgObj.getSELV());
					 msgObj.setSKNT(((msgObj.getSKNT()==null)||(msgObj.getSKNT().equals("")))?"N/A":msgObj.getSKNT());
					 msgObj.setTMPF(((msgObj.getTMPF()==null)||(msgObj.getTMPF().equals("")))?"N/A":msgObj.getTMPF());
					 msgObj.setWTHR(((msgObj.getWTHR()==null)||(msgObj.getWTHR().equals("")))?"N/A":msgObj.getWTHR());
					 wf.writeFile(msgObj);
					 ist.updateStationData(msgObj);
				 }
			 }
			 else if (json.get("type").equals("detail"))
			 {
				 StationData st = new StationData();
				 for(int i=0;i<keyArray.size();i++)
				 {
					 jkey = (String) iter.next();
					 jval = (String) json.get(jkey); 
					 try {
						 statDataObjMeth = stationDataClass.getMethod("set"+(jkey.trim()), new Class[]{String.class});
						 statDataObjMeth.invoke(st, jval);
					 } catch (NoSuchMethodException e) {
						 System.out.println("Ignore value not present");
						 continue;
					 }
				 }
				 ist.addNewStation(st);
			 }
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
