package flounder.collada;

import flounder.animation.*;
import flounder.collada.animation.*;
import flounder.collada.geometry.*;
import flounder.collada.joints.*;
import flounder.collada.skin.*;
import flounder.parsing.xml.*;
import flounder.resources.*;

public class ColladaLoader {
	public static ModelAnimated loadColladaModel(MyFile colladaFile) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), AnimationSettings.MAX_WEIGHTS);
		SkinningData skinningData = skinLoader.extractSkinData();

		JointsLoader jointsLoader = new JointsLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
		JointsData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		return new ModelAnimated(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(MyFile colladaFile) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);

		AnimationLoader a = new AnimationLoader(node.getChild("library_animations"));
		return a.extractAnimation();
	}
}
