﻿#pragma once

#include <functional>
#include "../Fonts/Text.hpp"
#include "../Guis/Gui.hpp"
#include "InputDelay.hpp"
#include "UiObject.hpp"

namespace Flounder
{
	class F_EXPORT IGrabber
	{
	public:
		virtual ~IGrabber() = default;

		virtual int GetCurrent(Text *object) = 0;

		virtual std::string GetValue(const int &value) = 0;
	};

	class F_EXPORT GrabberJoystick :
		public IGrabber
	{
	private:
		unsigned int m_joystick;
	public:
		GrabberJoystick(const unsigned int &joystick);

		int GetCurrent(Text *object) override;

		std::string GetValue(const int &value) override;
	};

	class F_EXPORT GrabberKeyboard :
		public IGrabber
	{
	public:
		int GetCurrent(Text *object) override;

		std::string GetValue(const int &value) override;
	};

	class F_EXPORT GrabberMouse :
		public IGrabber
	{
	public:
		int GetCurrent(Text *object) override;

		std::string GetValue(const int &value) override;
	};

	class F_EXPORT InputGrabber :
		public UiObject
	{
	private:
		static const float CHANGE_TIME;
		static const float SCALE_NORMAL;
		static const float SCALE_SELECTED;
		static Colour *const COLOUR_NORMAL;

		Text *m_text;
		Gui *m_background;

		IGrabber *m_grabber;

		std::string m_prefix;
		int m_value;

		InputDelay *m_inputDelay;
		int m_lastKey;

		bool m_selected;
		bool m_mouseOver;

		std::function<void()> m_actionChange;
	public:
		InputGrabber(UiObject *parent, const Vector3 &position, const std::string &prefix, const int &value, IGrabber *grabber, const Justify &justify);

		~InputGrabber();

		void UpdateObject() override;

		std::string GetPrefix() const { return m_prefix; }

		void SetPrefix(const std::string &prefix);

		int GetValue() const { return m_value; }

		void SetValue(const int &value);

		void SetActionChange(std::function<void()> action) { m_actionChange = action; }
	};
}
