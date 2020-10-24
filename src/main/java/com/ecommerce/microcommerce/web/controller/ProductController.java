package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


@Api( description="API pour es opérations CRUD sur les produits.")

@RestController
public class ProductController {

    @Autowired
    private ProductDao productDao;
    private Product product;


    //Récupérer la liste des produits

    @RequestMapping(value = "/Produits", method = RequestMethod.GET)

    public MappingJacksonValue listeProduits() {

        Iterable<Product> produits = productDao.findAll();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }


    //Récupérer un produit par son Id
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Produits/{id}")

    public Product afficherUnProduit(@PathVariable int id) {

        Product produit = productDao.findById(id);

        if(produit==null) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");

        return produit;
    }

    /*  Déclaration de la value de l'ApiOperation
    * Déclaration du type de requete (GET)
    * J'instancie une HashMap dans laquelle j'associe un objet Produit à un entier correspondant à la marge = Prix d'achat - prix de vente
    * En parcourant la lite des produits grace a findAll() de ProductDAO, j'affecte à chaque produit la différence entre son prix d'Achat et son prix de vente
    * Retourne enfin la hashMap remplie ce qui constitue la réponse à la requete sur l'URI "AdminProduits"
    * */

    @ApiOperation(value = "Affichage de la marge pour chaque produits")
    @RequestMapping(value = "/AdminProduits", method = RequestMethod.GET)
    public Map <Product,Integer> calculerMargeProduit() {
        Map <Product,Integer> listeMarges = new HashMap<Product,Integer>();
        List <Product> products = productDao.findAll();
         for(Product product : products) {
             listeMarges.put(product, (product.getPrixAchat() - product.getPrix()));
         }

         return listeMarges;
    }

        //Appel de la méthode de ProductDao suivant la convention de nommage
    @ApiOperation(value = "Tri par ordre alphabétique et affichage")
    @GetMapping(value = "/TriProduits")
    public List<Product>  trierProduits() {
        return productDao.findAllByOrderByNomAsc();
    }

    //ajouter un produit
    @PostMapping(value = "/Produits")

    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {
    // Définition de la condition levant l'exception sur le prix nul
        if(product.getPrix() == 0)
            throw new ProduitGratuitException("Le produit saisi a un prix égal à 0");

        Product productAdded =  productDao.save(product);

        if (productAdded == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping (value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id) {

        productDao.delete(id);
    }

    @PutMapping (value = "/Produits")
    public void updateProduit(@RequestBody Product product) {

        productDao.save(product);
    }


    //Pour les tests
    @GetMapping(value = "test/produits/{prix}")
    public List<Product>  testeDeRequetes(@PathVariable int prix) {

        return productDao.chercherUnProduitCher(400);
    }



}
