Jenkins.instance.getItems().each {
    def pipeline = it
    pipeline.allJobs.each {
        def job = it
        job.builds.each { it.delete() }
        job.nextBuildNumber = 1
        job.save()
    }
}