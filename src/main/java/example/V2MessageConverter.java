package example;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v251.datatype.CX;
import ca.uhn.hl7v2.model.v251.datatype.XPN;
import ca.uhn.hl7v2.model.v251.datatype.TS;
import ca.uhn.hl7v2.model.v251.datatype.IS;
import ca.uhn.hl7v2.model.v251.datatype.FN;
import ca.uhn.hl7v2.model.v251.datatype.NM;
import ca.uhn.hl7v2.model.v251.message.OUL_R22;
import ca.uhn.hl7v2.model.v251.group.OUL_R22_PATIENT;
import ca.uhn.hl7v2.model.v251.group.OUL_R22_SPECIMEN;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import ca.uhn.hl7v2.model.v251.segment.PID;
import ca.uhn.hl7v2.model.v251.segment.OBX;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Hl7InputStreamMessageIterator;

import example.pojo.patient.Patient;
import example.pojo.patient.Identifier;
import example.pojo.patient.Name;
import example.pojo.observation.Observation;
import example.pojo.observation.ValueQuantity;
import example.pojo.observation.Code;
import example.pojo.observation.Coding_;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.lang.Double;

public class V2MessageConverter{
  private Patient patient;
  private Observation[] observations;
  
  public V2MessageConverter(InputStream is){
    is = new BufferedInputStream(is);
//    HapiContext context = new DefaultHapiContext();
//    Parser p = context.getGenericParser();
    Hl7InputStreamMessageIterator iter = new Hl7InputStreamMessageIterator(is);
    while (iter.hasNext()){
      try{
        Message hapiMsg = iter.next();
        OUL_R22 oulMsg = (OUL_R22)hapiMsg;
        MSH msh = oulMsg.getMSH();
        String msgType = msh.getMessageType().getMessageCode().getValue();
        String msgEvent = msh.getMessageType().getTriggerEvent().getValue();
        System.out.println("msgType=" + msgType + "^" + msgEvent);
        
        PID pid = oulMsg.getPATIENT().getPID();
        CX patientID = pid.getPid3_PatientIdentifierList(1);
        XPN patientName = pid.getPid5_PatientName(1);
        TS dateTimeOfBirth = pid.getPid7_DateTimeOfBirth();
        IS administrativeSex = pid.getPid8_AdministrativeSex();
        System.out.println("patientName=" + patientName);
        
        List<Identifier> patientIDList = new ArrayList<Identifier>();
        Identifier identifier = new Identifier();
        identifier.setValue(patientID.getIDNumber().getValue());
        patientIDList.add(identifier);
        patient.setIdentifier(patientIDList);
        
        //Patient Name
        List<Name> patientNameList = new ArrayList<Name>();
        Name name = new Name();
        name.setFamily(patientName.getXpn1_FamilyName().getFn1_Surname().getValue());
        List<String> givenNameList = new ArrayList<String>();
        givenNameList.add(patientName.getXpn2_GivenName().getValue());
        name.setGiven(givenNameList);
        patientNameList.add(name);
        patient.setName(patientNameList);
        
        //DOB
        Date dob = dateTimeOfBirth.getTime().getValueAsDate();
        String strDob = new SimpleDateFormat("yyyyMMddhhmmss").format(dob);
        patient.setBirthDate(strDob);
        
        //Sex
        switch(administrativeSex.getValue()){
          case "M":
            patient.setGender("male");
            break;
          case "F":
            patient.setGender("female");
            break;
          case "O":
            patient.setGender("other");
            break;
          case "U":
            patient.setGender("unknown");
            break;
          default:
            patient.setGender("unknown");
            break;            
        }
        
        //Observation Loop
        List<Observation> observationList = new ArrayList<Observation>();
        for (int i = 0; i < oulMsg.getSPECIMENReps(); i++){
          Observation observation = new Observation();
          OBX obx = oulMsg.getSPECIMEN(i).getOBX();
          //OBX-2 skipped
          
          //OBX-3
          Coding_ coding = new Coding_();
          coding.setCode(obx.getObx3_ObservationIdentifier().getCe1_Identifier().getValue());
          coding.setDisplay(obx.getObx3_ObservationIdentifier().getCe2_Text().getValue());
          List codings = new ArrayList<Coding_>();
          codings.add(coding);
          Code code = new Code();
          code.setCoding(codings);
          observation.setCode(code);
          //OBX-5 & OBX-6
          ValueQuantity valueQuantity = new ValueQuantity();
          Varies customValue = obx.getObx5_ObservationValue(1);
          NM numericValue = (NM)customValue.getData();
          Double value = new Double(numericValue.getValue());
          valueQuantity.setValue(value);
          valueQuantity.setUnit(obx.getObx6_Units().getCe2_Text().getValue());
          observation.setValueQuantity(valueQuantity);
          
          //OBX-11
          switch(obx.getObx11_ObservationResultStatus().getValue()){
            case "F":
            observation.setStatus("final");
            break;
          default:
            observation.setStatus("unknown");
            break;            
          }
          
          //OBX-14
          Date observationDate = obx.getObx14_DateTimeOfTheObservation().getTime().getValueAsDate();
          String strObservationDate = new SimpleDateFormat("yyyyMMddhhmmss").format(observationDate);
          observation.setEffectiveDateTime(strObservationDate);
          
          observationList.add(observation);
        }
        observations = observationList.toArray(new Observation[observationList.size()]);

      // }catch(EncodingNotSupportedException e){
      //   e.printStackTrace();
      //   return;
      }
      catch(Exception e){
        e.printStackTrace();
        return;
      }
    }
      
  }
  
  public Patient getPatient(){
    return patient;
  }

    
  public Observation[] getObservations(){
    return observations;
  }
}