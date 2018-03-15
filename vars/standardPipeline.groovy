def call(body) {

	def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
	body.delegate = config
	body()

	node {
		// Clean workspace before doing anything
		deleteDir()

		try {
			stage ('Clone') {
				echo 'Checking out ' + config.repoUrl + ' ...'
				git config.repoUrl
			}
			stage ('Build') {
				echo 'building ' + config.projectName + ' ...'
			}
			stage ('Tests') {
				parallel 'static': {
					echo 'shell scripts to run static tests...'
				},
				'unit': {
					echo 'shell scripts to run unit tests...'
				},
				'integration': {
					echo 'shell scripts to run integration tests...'
				}
			}
			stage ('Deploy') {
				echo 'deploying to server ${config.serverDomain}...'
			}
		} catch (err) {
			currentBuild.result = 'FAILED'
			throw err
		}
	}
}