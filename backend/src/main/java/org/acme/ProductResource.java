package org.acme;

import jakarta.enterprise.inject.build.compatible.spi.Validation;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.Model.Product;
import org.acme.Service.ProductService;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<Product> getProducts(){
//        return productService.findAllProducts();
//    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response updateProduct(
            @PathParam("id") Long id,
            Product updatedProduct
    ){

        boolean updated = productService.updateProduct(id, updatedProduct);

        if(updated){
            return Response.ok().entity(updatedProduct).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

//    @DELETE
//    @Path("/{id}")
//    @Transactional
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response deleteProduct(@PathParam("id") Long id){
//
//        boolean deleted = productService.deleteProduct(id);
//
//        if(deleted){
//            return Response.noContent().build();
//        }
//
//        return Response.status(Response.Status.NOT_FOUND).build();
//
//    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProduct(Product product){

        productService.createProduct(product);

        return Response.status(Response.Status.CREATED).entity(product).build();

    }

}
