package org.uniof.manchester.pattern.web.core;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.uniof.manchester.pattern.web.Box;
import org.uniof.manchester.pattern.web.BoxEntity;
import org.uniof.manchester.pattern.web.BoxFactory;
import org.uniof.manchester.pattern.web.ExtraParts;
import org.uniof.manchester.pattern.web.Furniture;
import org.uniof.manchester.pattern.web.Material;
import org.uniof.manchester.pattern.web.Piece;
import org.uniof.manchester.pattern.web.database.AccessDatabaseManager;
import org.uniof.manchester.pattern.web.database.DatabaseManager;

/**
 * Servlet implementation class RegisterOrderDetails
 */
@WebServlet("/RegisterOrderDetails")
public class RegisterOrderDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Logger LOG = Logger.getLogger(RegisterClient.class);

	@Resource(name = "jdbc/furniture_planner")
	private DataSource dataSource;

	public RegisterOrderDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Connection conn = null;

		try {
			conn = getConnection();
			AccessDatabaseManager dbManager = new DatabaseManager();

			// Add a new temporary material. A set of materials should be stored in the
			// database
			ArrayList<Material> mats = new ArrayList<Material>();
			Material mat = new Material(0, "melamine", "black", 5);
			mats.add(mat);

			// Add a new temporary extra part. A set of extra parts should be stored in the
			// database
			ArrayList<ExtraParts> exts = new ArrayList<ExtraParts>();
			for (int l = 0; l < 15; l++) {
				ExtraParts ext = new ExtraParts(0, "wheel", 1f, "K");
				exts.add(ext);
			}

			// ----------------------------------------------------------------------------------------

			// firstly, get the number of furniture

			Integer num_furns = Integer.valueOf(request.getParameter("num_furniture"));

			// TODO: Materials and Extraparts should be fetched from DB
			BoxFactory factory = new BoxFactory();

			ArrayList<Furniture> furns = new ArrayList<Furniture>();

			for (int i = 1; i <= num_furns; i++) {

				System.out.println(">>>Furniture " + i);

				// for each furniture, get the number of boxes
				Integer num_boxes = Integer.valueOf(request.getParameter("num_boxes" + i));
				String fur_name = (String) request.getParameter("fur_name" + i);
				String fur_kind = (String) request.getParameter("fur_kind" + i);

				System.out.println(">>>Num Boxes " + num_boxes);

				ArrayList<Box> boxes = new ArrayList<Box>();

				// create a new Furniture and add it to the order list
				Furniture furn = new Furniture(fur_name, 0, 0, boxes);

				for (int j = 1; j <= num_boxes; j++) {

					String box_type = (String) request.getParameter("sel_box" + i + j);
					double box_height = Double.valueOf(request.getParameter("box_height" + i + j));
					double box_width = Double.valueOf(request.getParameter("box_width" + i + j));
					double box_depth = Double.valueOf(request.getParameter("box_depth" + i + j));
					double box_thick = Double.valueOf(request.getParameter("box_thick" + i + j));
					String melamine_colour = (String) request.getParameter("mel" + i + j);
					String door_colour = (String) request.getParameter("box_colour" + i + j);
					String box_material = (String) request.getParameter("material" + i + j);

					System.out.println("Box Type: " + box_type);
					System.out.println("Box height: " + box_height);
					System.out.println("Box width: " + box_width);
					System.out.println("Box depth: " + box_depth);
					System.out.println("Box thickness: " + box_thick);
					System.out.println("Box colour: " + melamine_colour);
					System.out.println("Door colour: " + door_colour);
					System.out.println("Box material: " + box_material);

					// box gets furniture type?
					Box box = new Box(1, box_type, box_height, box_width, box_depth, box_thick, melamine_colour,
							door_colour, new ArrayList<Piece>(), exts);

					BoxEntity calc_box = factory.createBox(box_type, box_height, box_width, box_depth, box_thick,
							melamine_colour, door_colour, exts, mat.getName(), "K");

					if (calc_box == null)
						System.out.println("calc_box is null");

					// Num shelves, drawers???
					/*
					 * calc_box.calculatePieces(box_height, box_width, box_depth, box_thick,
					 * melamine_colour, door_colour, mat.getName(), 4, 4);
					 * 
					 * calc_box.calculateExtraParts(exts, 4, 4);
					 */

					// box gets all the calculated pieces and extras from calc_box
					// box.setPieces(calc_box.getPieces());
					// box.setExtras(calc_box.getExtras());

					boxes.add(box);

				}

				furn.setBoxes(boxes);
				furns.add(furn);

			}

			// Finally, the Order object is created
			// Order order = new Order(0, null, Integer.valueOf(clientId), status_code,
			// null, 0, order_name );

			// redirect to the order page
			// RequestDispatcher requestDispatcher =
			// request.getRequestDispatcher("/order.jsp");
			// requestDispatcher.forward(request, response);

		} catch (SQLException e) {
			LOG.error("Problemas en la generaci�n del excel de calificaciones desde SQL " + e);
			throw new RuntimeException("SQL error : " + e.getMessage());
		} catch (Exception e) {
			LOG.fatal("Excepci�n al generar el reporte : " + e.getMessage());
			throw new RuntimeException("Excepci�n al generar el reporte : " + e.getMessage());
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				LOG.error("Problemas al cerrar la conexi�n despues de crear el excel de calificaciones,", e);
				throw new ServletException(e.getMessage(), e);
			}
		}

	}

	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

}