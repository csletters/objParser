import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;



public class objReader {
	
	public static void createBinaryFile() throws FileNotFoundException, IOException
	{
		float[] vertices;
		float[] normals;
		float[] uvCords;

		List<String> lines;
		ArrayList<Integer> offsetVertex = new ArrayList<Integer>();
		ArrayList<Integer> offsetNormal = new ArrayList<Integer>();
		ArrayList<Integer> offsetUV = new ArrayList<Integer>();
		
		lines = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader("parsedObj.txt"))) 
		{
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		        	if(line.length() > 3)
						lines.add(line);
		            line = br.readLine();
		        }
		}
		
		//get total model size
		String[] tokens = lines.get(lines.size()-1).split("[ ]+");
		vertices = new float[Integer.parseInt(tokens[0])];
		uvCords= new float[Integer.parseInt(tokens[1])];
		normals = new float[Integer.parseInt(tokens[2])];
		
		//get num models
		int numModels = lines.size()/4;
		//index
		int indexVert =0;
		int indexUV = 0;
		int indexNormal = 0;
		for(int x = 0; x< numModels;x++)
		{
			//vertices
			tokens = lines.get(x*4).split("[ ]+");
			for(int i =0; i < tokens.length;i++)
			{
				vertices[indexVert] = Float.parseFloat(tokens[i]);
				indexVert++;
			}
			//uv
			tokens = lines.get((x*4)+1).split("[ ]+");
			for(int i =0; i < tokens.length;i++)
			{
				uvCords[indexUV] = Float.parseFloat(tokens[i]);
				indexUV++;
			}
			//normals
			tokens = lines.get((x*4)+2).split("[ ]+");
			for(int i =0; i < tokens.length;i++)
			{
				normals[indexNormal] = Float.parseFloat(tokens[i]);
				indexNormal++;
			}
			
			//offset
			tokens = lines.get((x*4)+3).split("[ ]+");
			offsetVertex.add(Integer.parseInt(tokens[0]));
			offsetUV.add(Integer.parseInt(tokens[1]));
			offsetNormal.add(Integer.parseInt(tokens[2]));
		}
		
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		 try {
			 	fos = new FileOutputStream("BinaryModel");
			 	
			 	// create data output stream
		        dos = new DataOutputStream(fos);
	           
		        
		        //write numVertices, numUV, numNormals
		        dos.writeFloat((float)vertices.length);
		        dos.writeFloat((float)uvCords.length);
		        dos.writeFloat((float)normals.length);
		        dos.writeFloat((float)offsetVertex.get(0));
		      
		        // for each byte in the buffer
		        for(int x = 0; x < vertices.length;x++)
		        {
		            // write float to the dos
		            dos.writeFloat(vertices[x]);         
		        }
		        
		        // for each byte in the buffer
		        for(int x = 0; x < uvCords.length;x++)
		        {
		            // write float to the dos
		            dos.writeFloat(uvCords[x]);         
		        }
		        
		        // for each byte in the buffer
		        for(int x = 0; x < normals.length;x++)
		        {
		            // write float to the dos
		            dos.writeFloat(normals[x]);         
		        }
		        dos.flush();
		        fos.close();
		        
	        } catch (IOException e) {}
	}
	
	
	public static Model createModelValues(String filename) throws FileNotFoundException, IOException
	{
		String txtfile = "";
		List<String> lines;
		List<Vertex> vert;
		List<TexCords> tex;
		List<Normal> norm;
		List<Polygon> poly;
		float[] vertices;
		float[] normals;
		float[] uv;
		float[] uvSorted;
		float[] normalsSorted;
		float[] verticesSorted;
		short[] facesVerts;
		short[] facesNormals;
		short[] facesUV ;
	    int vertexIndex = 0;
	    int normalIndex =0;
	    int uvIndex = 0;
		int faceIndex = 0;
		
		lines = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) 
		{
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		        	lines.add(line);
		            line = br.readLine();
		        }
		        txtfile = sb.toString();
		}
		
		
		//parse the file
		vertices = new float[lines.size() *3];
		normals = new float[lines.size() *3];
		uv = new float[lines.size()*2];
        facesVerts = new short[lines.size() * 3];
        facesNormals = new short[lines.size() * 3];
        facesUV = new short[lines.size() * 3];
        
        vert = new ArrayList<Vertex>();
        tex = new ArrayList<TexCords>();
        poly = new ArrayList<Polygon>();
        norm = new ArrayList<Normal>();
        for(int i = 0; i < lines.size(); i++)
        {
        	Vertex tempv = new Vertex();
        	TexCords tempt = new TexCords();
        	Normal tempn = new Normal();
        	
        	Polygon tempp = new Polygon();
        	String line = lines.get(i);
        	
        	if(line.startsWith("v "))
        	{
        		String[] tokens = line.split("[ ]+");
        		tempv.x = Float.parseFloat(tokens[1]);
        		tempv.y = Float.parseFloat(tokens[2]);
        		tempv.z = Float.parseFloat(tokens[3]);
        		vert.add(tempv);

        	}
        	if(line.startsWith("vn "))
        	{
        		String[] tokens = line.split("[ ]+");
        		tempn.x = Float.parseFloat(tokens[1]);
        		tempn.y = Float.parseFloat(tokens[2]);
        		tempn.z = Float.parseFloat(tokens[3]);
                norm.add(tempn);
        	}
            if (line.startsWith("vt ")) {
                String[] tokens = line.split("[ ]+");
                tempt.x = Float.parseFloat(tokens[1]);
                tempt.y = Float.parseFloat(tokens[2]);
                tex.add(tempt);
            }
            
            if (line.startsWith("f ")) {
                String[] tokens = line.split("[ ]+");
                
                String[] parts = tokens[1].split("/");
                tempp.vx = vert.get((short) (Short.parseShort(parts[0])-1)).x;
                tempp.vy = vert.get((short) (Short.parseShort(parts[0])-1)).y;
                tempp.vz = vert.get((short) (Short.parseShort(parts[0])-1)).z;
                
                if (parts.length > 2)
                {
                	if(parts[2].equals(""))
                	{}
                	else
                	{
                		tempp.nx = norm.get((short) (Short.parseShort(parts[2])-1)).x;
                		tempp.ny = norm.get((short) (Short.parseShort(parts[2])-1)).y;
                		tempp.nz = norm.get((short) (Short.parseShort(parts[2])-1)).z;
                	}
                }
                if (parts.length > 1)
                {
                	if(parts[1].equals(""))
                	{}
                	else
                	{
                		tempp.tx = tex.get((short) (Short.parseShort(parts[1])-1)).x;
                		tempp.ty = tex.get((short) (Short.parseShort(parts[1])-1)).y;
                	}
                }
                poly.add(tempp);
                faceIndex++;
 
                Polygon tempp2 = new Polygon();
                parts = tokens[2].split("/");
                tempp2.vx = vert.get((short) (Short.parseShort(parts[0])-1)).x;
                tempp2.vy = vert.get((short) (Short.parseShort(parts[0])-1)).y;
                tempp2.vz = vert.get((short) (Short.parseShort(parts[0])-1)).z;
                
                if (parts.length > 2)
                {
                	if(parts[2].equals(""))
                	{}
                	else
                	{
                	tempp2.nx = norm.get((short) (Short.parseShort(parts[2])-1)).x;
                	tempp2.ny = norm.get((short) (Short.parseShort(parts[2])-1)).y;
                	tempp2.nz = norm.get((short) (Short.parseShort(parts[2])-1)).z;
                	}
                }
                if (parts.length > 1)
                {
                	if(parts[1].equals(""))
                	{}
                	else
                	{
                	tempp2.tx = tex.get((short) (Short.parseShort(parts[1])-1)).x;
                	tempp2.ty = tex.get((short) (Short.parseShort(parts[1])-1)).y;
                	}
                }
                poly.add(tempp2);
                faceIndex++;

                Polygon tempp3 = new Polygon();
                parts = tokens[3].split("/");
                tempp3.vx = vert.get((short) (Short.parseShort(parts[0])-1)).x;
                tempp3.vy = vert.get((short) (Short.parseShort(parts[0])-1)).y;
                tempp3.vz = vert.get((short) (Short.parseShort(parts[0])-1)).z;
                
                if (parts.length > 2)
                {
                	if(parts[2].equals(""))
                	{}
                	else
                	{
                	tempp3.nx = norm.get((short) (Short.parseShort(parts[2])-1)).x;
                	tempp3.ny = norm.get((short) (Short.parseShort(parts[2])-1)).y;
                	tempp3.nz = norm.get((short) (Short.parseShort(parts[2])-1)).z;
                	}
                }
                if (parts.length > 1)
                {
                	if(parts[1].equals(""))
                	{}
                	else
                	{
                	tempp3.tx = tex.get((short) (Short.parseShort(parts[1])-1)).x;
                	tempp3.ty = tex.get((short) (Short.parseShort(parts[1])-1)).y;
                	}
                }
                poly.add(tempp3);
                faceIndex++;
            }

        }
        uvSorted =  new float[faceIndex*2];
        verticesSorted = new float[faceIndex*3];
        normalsSorted = new float[faceIndex*3];
        int index = 0;
        for(int x = 0; x < faceIndex; x++ )
        {
        	verticesSorted[index] = poly.get(x).vx;
        	index++;
        	verticesSorted[index] = poly.get(x).vy;
        	index++;
        	verticesSorted[index] = poly.get(x).vz;
        	index++;
        }
        index = 0;
        for(int x = 0; x < faceIndex; x++ )
        {
        	uvSorted[index] = poly.get(x).tx;
        	index++;
        	uvSorted[index] = poly.get(x).ty;
        	index++;
        }
        index = 0;
        for(int x = 0; x < faceIndex; x++ )
        {
        	//normalize
        	double length = Math.sqrt((poly.get(x).nx*poly.get(x).nx) +(poly.get(x).ny*poly.get(x).ny) +(poly.get(x).nz*poly.get(x).nz));
        	if(length == 0)
        	{
        		normalsSorted[index] = 0.0f;
        		index++;
        		normalsSorted[index] = 0.0f;
        		index++;
        		normalsSorted[index] = 0.0f;
        		index++;
        	}
        	else
        	{
        		normalsSorted[index] = (float) (poly.get(x).nx/length);
        		index++;
        		normalsSorted[index] = (float) (poly.get(x).ny/length);
        		index++;
        		normalsSorted[index] = (float) (poly.get(x).nz/length);
        		index++;
        	}
        }
        
        
		return (new Model(verticesSorted,uvSorted,normalsSorted));
		
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException 
	{
		ArrayList<Model> models = new ArrayList<Model>();
//		models.add(createModelValues("horse1.obj"));
//		models.add(createModelValues("horse2.obj"));
//		models.add(createModelValues("horse3.obj"));
//		models.add(createModelValues("horse4.obj"));
//		models.add(createModelValues("horse5.obj"));
//		models.add(createModelValues("horse6.obj"));
//		models.add(createModelValues("horse7.obj"));
//		models.add(createModelValues("horse8.obj"));
//		models.add(createModelValues("horse9.obj"));
//		models.add(createModelValues("horse10.obj"));
//		models.add(createModelValues("horse11.obj"));
//		models.add(createModelValues("horse12.obj"));
//		models.add(createModelValues("horse13.obj"));
//		models.add(createModelValues("horse14.obj"));
		//models.add(createModelValues("crown_victoria.obj"));
		//models.add(createModelValues("ring.obj"));
		models.add(createModelValues("sphere.obj"));
        
		int totalVert =0,totalNormals=0,totalUV=0;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("parsedObj.txt"));
            for(int x = 0; x < models.size(); x++)
            {
                for (int i = 0; i < models.get(x).verticesSorted.length; i++) {
                	if(i == models.get(x).verticesSorted.length-1 )
                		 out.write(models.get(x).verticesSorted[i]+"");
                	else
                		out.write(models.get(x).verticesSorted[i] + " ");
                }
                out.newLine();
                for (int i = 0; i < models.get(x).uvSorted.length; i++) {
                	if(i == models.get(x).uvSorted.length-1 )
                		out.write(models.get(x).uvSorted[i]+"");
                	else
                		out.write(models.get(x).uvSorted[i] + " ");
                }
                out.newLine();
                for (int i = 0; i < models.get(x).normalsSorted.length; i++) {
                	if(i == models.get(x).normalsSorted.length-1 )
                		out.write(models.get(x).normalsSorted[i]+"");
                	else
                		out.write(models.get(x).normalsSorted[i] + " ");
                }
                totalVert += models.get(x).verticesSorted.length;
                totalNormals += models.get(x).uvSorted.length;
                totalUV += models.get(x).normalsSorted.length;
                out.newLine();
                out.write(models.get(x).verticesSorted.length +" "+  models.get(x).uvSorted.length+ " "+  models.get(x).normalsSorted.length);
                out.newLine();
            }
            out.write(totalVert +" "+  totalNormals+ " "+  totalUV);
            out.close();
            } catch (IOException e) {}
        
        createBinaryFile();
      
	}

}
