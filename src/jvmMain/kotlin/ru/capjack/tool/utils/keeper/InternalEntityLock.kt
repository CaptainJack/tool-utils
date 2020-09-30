package ru.capjack.tool.utils.keeper

import ru.capjack.tool.utils.Cancelable

class InternalEntityLock {
	@Volatile
	var killer: Cancelable = Cancelable.DUMMY
	
	@Volatile
	var touched = false
	
	@Volatile
	var holds = 0
}