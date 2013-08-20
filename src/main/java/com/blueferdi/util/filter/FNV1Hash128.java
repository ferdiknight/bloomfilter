/*
 * Copyright (C) 2013 guang.com
 */

package com.blueferdi.util.filter;

import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;

/**
 *
 * @author ferdi email ferdi@blueferdi.com
 * @version 0.0.1
 */
public class FNV1Hash128 
{
    //144066263297769815596495629667062367629
    public static final BigInteger FNV_BASIS = BigInteger.valueOf(1440662632977698155L).multiply(BigInteger.ONE.pow(20))
            .add(BigInteger.valueOf(964956296670623676L).multiply(BigInteger.TEN.pow(2))).add(BigInteger.valueOf(29));
    public static final BigInteger FNV_PRIME = BigInteger.valueOf(1L).shiftLeft(88).add(BigInteger.valueOf(0x13b));
    
    public static BigInteger hash(byte[] key) {
        BigInteger hash = FNV_BASIS;
        for(int i = 0; i < key.length; i++) {
            hash = hash.multiply(FNV_PRIME);
            hash = hash.xor(BigInteger.valueOf(0xFF & key[i]));
        }
        
        return (hash.equals(BigInteger.ZERO)) ? BigInteger.valueOf(Long.MAX_VALUE) : hash;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        FNV1Hash128 hash = new FNV1Hash128();
        
        File file = new File("F:/words.txt");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        
        String line = "";
        
        BigInteger filter = BigInteger.ZERO;
        
        int count = 0;
        
        while((line=reader.readLine()) != null)
        {
            String[] words = line.split(" ");
            
            for(String s: words)
            {
                filter = filter.or(hash.hash(s.getBytes()));
                count++;
            }
            
        }
        
        reader.close();
        System.out.println(count);
        System.out.println(filter.toString(16)  + " " + filter.toByteArray().length);
        
    }
    
}
