package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.circuitbreaker.delegate.ServiceDelegate;
import org.springframework.beans.factory.annotation.Autowired;


public class ServiceController {

    @Autowired
    ServiceDelegate serviceDelegate;

    /*@RequestMapping(value = "/Produits", method = RequestMethod.GET)
    public String getProduits() {
        System.out.println();
    }*/

}
