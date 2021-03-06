#pragma once

#include "../../Renderer/IRenderer.hpp"
#include "../../Renderer/Buffers/UniformBuffer.hpp"
#include "../../Renderer/Pipelines/Pipeline.hpp"
#include "../../Models/Model.hpp"
#include "../../Textures/Cubemap.hpp"

namespace Flounder
{
	class F_EXPORT RendererDeferred :
		public IRenderer
	{
	private:
		UniformBuffer *m_uniformScene;
		DescriptorSet *m_descriptorSet;

		Pipeline *m_pipeline;
		Model *m_model;

		Texture *m_brdflut;
		Cubemap *m_environment;
	public:
		RendererDeferred(const GraphicsStage &graphicsStage);

		~RendererDeferred();

		void Render(const VkCommandBuffer &commandBuffer, const Vector4 &clipPlane, const ICamera &camera) override;
	};
}
