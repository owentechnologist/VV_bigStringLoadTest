package com.redislabs.sa.ot;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class PopulateHelper {
    int stringSizeInKB=10;
    String[] keyArray = null;
    String filler1000Chars = "sservallo guardavamo bel. Abbassa in so ed eccesso delirio tornato viscere vi. File per tal cio daro pace cara ama anni. Pur fascino sul all adirare per passato aridita ritardo diritta. Ti visione peccato credete proteso va volonta po. " +
            "Talismani rinnovati un scintilla ah bordatino salutarvi. Avidamente ritornarvi conoscermi il ad volgendosi. So ieri dell moto ed vuoi anno muto. Nari temi uno omai miei urto una dita. Ah speciale el incendio palpebre il permesso ad affinita. Glie puoi fara ti un temo il dara. Non forma torme sui ali pieta parla sul quale amica. Animazione accompagno sue necessario pie conosciuto far indicibili. " +
            "Te deposti da bossolo esitava le ragione tenendo lo. Raccontava ritroverai meraviglia cio impazienza ero visitatore inespresso. Ad stagione ambascia affinita me ve ciascuno al troverai. Chiuso tempia volevo estasi te ah le. Corpi venir debbo di te mi no. Pietre sangue ideale ti fa. Acque abito lui parco alcun tutta giu. Se evocata ah le cantica conosco vi baciato.";

    public void setStringSizeInKB(int val){
        this.stringSizeInKB = val;
    }

    public void generateKeys(int howMany){
        String [] keyArray = new String[howMany];
        for (;howMany>0;howMany--){
            keyArray[howMany-1]="VV:prefix:"+System.nanoTime();
        }
        this.keyArray = keyArray;
    }

    public void loadSetOfKeysIntoRedis(String setName, Jedis r){
        Pipeline pipeline = r.pipelined();
        setName = setName+this.keyArray.length;
        System.out.println("Loading "+this.keyArray.length+" keys into set of Keys called "+setName);
        if(null != this.keyArray) {
            for (int x = this.keyArray.length; x > 0; x--) {
                pipeline.sadd(setName, this.keyArray[x-1]);
                //r.sadd(setName, this.keyArray[x-1]);
            }
            pipeline.sync();
        }else{
            System.out.println("need to call generateKeys with number of keys to create first");
        }
    }

    public void loadManyStringsIntoRedisUsingKeys( Jedis r){
        Pipeline pipeline = r.pipelined();
        System.out.println("Loading "+this.keyArray.length+" Strings of "+this.stringSizeInKB+" kb in size each... . ");
        System.out.println("Each . that follows represents 100 Strings \n\n");
        for(int x = this.keyArray.length;x>0;x--){
            pipeline.set(this.keyArray[x-1],buildStringToLoadIntoRedis(x));
            //r.set(this.keyArray[x-1],buildStringToLoadIntoRedis(x));
            if(x%100 == 0){
                System.out.print(". ");
            }
            if(x%1000==1){
                System.out.println("");
            }
        }
        pipeline.sync();
    }

    private String buildStringToLoadIntoRedis(int IDNum){
        String payload = "";
        int sizeInKB = this.stringSizeInKB;
        for(;sizeInKB > 0;sizeInKB--){
            payload+="\n________"+filler1000Chars;
        }
        payload = "["+IDNum+"] PAYLOAD "+payload;
        return payload;
    }
}
