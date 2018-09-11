/*
 * Copyright (C) 2018 Marcus Hirt
 *                    www.hirt.se
 *
 * This software is free:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESSED OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright (C) Marcus Hirt, 2018
 */
package se.hirt.examples.robotshop.order.rest;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import se.hirt.examples.robotshop.common.data.RealizedOrder;
import se.hirt.examples.robotshop.common.data.RobotOrder;
import se.hirt.examples.robotshop.common.util.Utils;
import se.hirt.examples.robotshop.order.OrderManager;

/**
 * Rest API for orders that are ready for pickup.
 * 
 * @author Marcus Hirt
 */
@Path("/readyorders/")
public class RealizedOrdersResource {
	@Context
	UriInfo uriInfo;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JsonArray list() {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (RealizedOrder order : OrderManager.getInstance().getCompletedOrders()) {
			arrayBuilder.add(order.toJSon());
		}
		return arrayBuilder.build();
	}

	@Path("{robotOrderId}/")
	public RealizedOrderResource getOrder(@PathParam(RobotOrder.KEY_ORDER_ID) Long robotOrderId) {
		return new RealizedOrderResource(uriInfo, robotOrderId);
	}

	@GET
	@Path("/pickup")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buildRobot(@QueryParam(RobotOrder.KEY_ORDER_ID) Long orderId) {
		if (orderId == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(Utils.errorAsJSonString(RobotOrder.KEY_ORDER_ID + " must not be null!")).build();
		}
		RealizedOrder robotOrder = OrderManager.getInstance().pickUpOrder(orderId);
		if (robotOrder == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(robotOrder.toJSon().build()).build();
	}
}
