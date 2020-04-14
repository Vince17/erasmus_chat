/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package addon;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
/*
 * @author damie
 */
public class MyDate{
    
    LocalDateTime date;
    
    DateTimeFormatter  toString = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    DateTimeFormatter  toSend = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
    
    public MyDate(int year, int mounth, int day, int hour, int minute, int second){
        date = LocalDateTime.of(year, mounth, day, hour, minute, second);
    }
    
    public MyDate(LocalDateTime _date){
        date = _date;
    }
    
    public MyDate(java.sql.Timestamp _date){
    	System.out.println("not yet converted");
        date = ToLocalDateTime(_date);
    	System.out.println("converted");
    }
    
    public MyDate(String sDate){
        String[] dateFrac = sDate.split("-");
        int[] dateFracInt = new int[6];
        for(int i = 0; i<6; i++){
            dateFracInt[i] = Integer.parseInt(dateFrac[i]);
        }
        date = LocalDateTime.of(dateFracInt[2],dateFracInt[1],dateFracInt[0],dateFracInt[3],dateFracInt[4],dateFracInt[5]);
    }
    
    public String ToPrint(){
        return toString.format(date);
    }
    
    public String ToSend(){
        return toSend.format(date);
    }
    
    public java.sql.Timestamp ToSQLTimestamp(){
        return java.sql.Timestamp.valueOf(date);
    }
    
    //take java.sql.MyDate and transform to java.time.LocalDateTime
    private LocalDateTime ToLocalDateTime(java.sql.Timestamp dateToConvert) {
    return dateToConvert.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDateTime();
    }
    
    public static MyDate now() {
    	return new MyDate(LocalDateTime.now());
    }
}
