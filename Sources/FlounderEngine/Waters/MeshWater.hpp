#pragma once

#include "Models/Shapes/MeshSimple.hpp"

namespace Flounder
{
	class F_EXPORT MeshWater :
		public MeshSimple
	{
	public:
		MeshWater();

		Vector3 GetPosition(const float &x, const float &z) override;

		Vector3 GetNormal(const Vector3 &position) override;

		Vector3 GetColour(const Vector3 &position, const Vector3 &normal) override;
	};
}