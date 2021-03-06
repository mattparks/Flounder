#pragma once

#include <map>
#include "../Maths/Colour.hpp"
#include "../Maths/Vector3.hpp"

namespace Flounder
{
	class Chunk;

	enum BlockFace
	{
		FaceFront = 0,
		FaceBack = 1,
		FaceUp = 2,
		FaceDown = 3,
		FaceLeft = 4,
		FaceRight = 5
	};

	class F_EXPORT Block
	{
	private:
		static std::map<std::string, Colour*> s_colours;

		Chunk *m_parent;

		Vector3 *m_position;
		std::string m_type;

	public:
		Block(Chunk *parent, const Vector3 &position, const std::string &type);

		~Block();

		void SetChanged();

		Vector3 *GetPosition() const { return m_position; }

		std::string GetType() const { return m_type; }

		void SetType(const std::string &type) { m_type = type; }

		static Colour *FindColour(const std::string &key);
	};
}
