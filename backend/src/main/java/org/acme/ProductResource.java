package org.acme;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.Model.Product;
import org.acme.Service.ProductService;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/page")
    public Response getProductsPage(
            @QueryParam("pageNo") int pageNo,
            @QueryParam("pageSize") int pageSize
    ){

        List<Product> products = productService.getPage(pageNo,pageSize);

        return Response.ok(products).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllProducts(){
        List<Product> products = productService.findAllProducts();
        return Response.ok(products).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/search")
    public Response getProductsByNameAndSku(
            @QueryParam("name") String name,
            @QueryParam("sku") String sku
    ){

        List<Product> products = productService.findAllProductsByNameAndSku(name,sku);
        return Response.ok(products).build();
    }

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

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteProduct(@PathParam("id") Long id){

        boolean deleted = productService.deleteProductById(id);

        if(deleted){
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();

    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProduct(Product product){
        Log.fatal("inside createProduct");
        boolean created = productService.createProduct(product);

        if(created){
            return Response.status(Response.Status.CREATED).entity(product).build();
        }

        return Response.status(BAD_REQUEST).build();

    }

}
