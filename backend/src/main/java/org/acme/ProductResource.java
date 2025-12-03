package org.acme;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.DTO.ProductPage;
import org.acme.Exception.GlobalExceptionHandler;
import org.acme.Model.Product;
import org.acme.Service.ProductService;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getProductsPage(
            @QueryParam("pageNo") int pageNo,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("name") String name,
            @QueryParam("sku") String sku,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDir") String sortDir
    ){

        ProductPage products = productService.fetchProductPage(pageNo,pageSize,name,sku,sortBy,sortDir);

        return Response.ok(products).build();
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProduct(Product product){

        if(productService.isSkuTaken( product.sku, null) ){
            return Response.status(409)
                    .entity(new GlobalExceptionHandler.ErrorPayload("A product with this SKU already exists.", 409))
                    .build();
        }

        boolean created = productService.createProduct(product);

        if(created){
            return Response.status(Response.Status.CREATED).entity(product).build();
        }

        return Response.status(BAD_REQUEST).build();

    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response updateProduct(
            @PathParam("id") Long id,
            Product updatedProduct
    ){

        if(productService.isSkuTaken( updatedProduct.sku, id) ){
            return Response.status(409)
                    .entity(new GlobalExceptionHandler.ErrorPayload("A product with this SKU already exists.", 409))
                    .build();
        }


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

}
