package org.acme;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/page")
    public List<Product> getProductsPage(
            @QueryParam("pageNo") int pageNo,
            @QueryParam("pageSize") int pageSize
    ){

        return productService.getPage(pageNo,pageSize);

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllProducts(){
        List<Product> products = productService.findAllProducts();
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

        boolean deleted = productService.deleteProduct(id);

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

        boolean result = productService.createProduct(product);

        return Response.status(Response.Status.CREATED).entity(product).build();

    }

}
